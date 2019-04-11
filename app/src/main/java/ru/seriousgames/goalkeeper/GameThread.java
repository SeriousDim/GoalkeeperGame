package ru.seriousgames.goalkeeper;
/*
 *  Property of Dmitrii Lykov(Дмитрий Лыков) aka Serious Games
 *  2018
 *
 *  Класс GameThread
 *  поток, выполняющий просчет физики, игровых стадий и взаимодействие
 *  между объектами игры, в том числе MySurfaceView, и игроком
 */

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.os.Handler;

import static ru.seriousgames.goalkeeper.MainActivity.h;

public class GameThread extends Thread {

    SurfaceHolder holder;
    final Graphics graphics;
    Canvas cnv;
    ObjectStorage storage;
    Values vals;
    long time;                          // хранит время начала кадра
    int showTextTimer;                  // хранит оставшееся время до окончания показа текста по центру
    boolean showText, finished;                   // нужно ли сейчас показывать текст по центру
    final int width, height;            // хранят ширину и высоту экрана
    // TODO: убрать, когда придет время
    //final float x, y;                 // ВОЗМОЖНО, БЕСПОЛЕЗЫНЫЕ ПЕРЕМЕННЫЕ: по умолчанию = 0
    //Handler h;
    MainActivity activity;
    Round round;

    int points, goals, totalGoals;      // очки, голы текущего раунда, всего голов

    boolean step1, step2, ballMove;     // игровые стадии: "стрелка", "сила" и период движения мяча
    private boolean controlLock;        // определяет, нужно ли отслеживать нажатия на экран

    // ### переменные для счетчика FPS ###
    int fps = 0;
    float mil = 0;
    // ### КОНЕЦ переменных ###

    public GameThread(SurfaceHolder holder, MainActivity activity, Graphics graphics, int width, int heigth){
        this.holder = holder;
        this.graphics = graphics;
        this.width = width;
        this.height = heigth;
        step1 = true;
        step2 = false;
        ballMove = false;
        finished = false;
        controlLock = true;
        //this.h = h;
        this.activity = activity;
        this.round = new Round(1);
        sendTextMessage(1, this.round.num, 2000);
        sendMessage(1);
        this.points = 0;
        this.goals = 0;
        this.totalGoals = 0;
    }

    @Override
    public void run()
    {
        // TODO: REFACTORING
        // ### теоретически, ЭТОТ код может быть в конструкторе ###
        vals = new Values(this.width, this.height, graphics.ff, graphics.field.getHeight());
        // TODO: переделать конструктор на ObjectStorage(float width, float height)
        this.storage = new ObjectStorage(
                new Ball(vals.BALL_CENTER_X, vals.BALL_CENTER_Y, graphics.ball.getWidth()/2f),
                new Gates(width, graphics.field.getHeight(), graphics.ff),
                new GatePlayer(width, graphics.field.getHeight())
        );
        storage.forceArrowX = vals.FORCE_ARROW_START_X;
        // ### КОНЕЦ ЭТОГО кода ###

        // ### игровой цикл: связан с временем ###
        while(!this.isInterrupted())                // пока поток не завершен
        {
            long now = System.currentTimeMillis();
            long elapsed = now - this.time;
            this.time = now;

            cnv = null;
            try
            {
                cnv = holder.lockCanvas();      // берем canvas
                synchronized (holder)           // чтобы holder мог использоваться только этим потоком: MySurfaceView не сможет его использовать
                {                               // взаимодействуем с canvas
                    update(elapsed);
                    draw(cnv);
                    countFPS(elapsed);
                }
            }
            finally
            {
                if(cnv != null)
                    holder.unlockCanvasAndPost(cnv);    // отдаем canvas
            }
        }
        // ### КОНЕЦ игрового цикла ###
    }

    // ### счетчик FPS ###
    private final void countFPS(long elapsed)
    {
        if(mil >= 1000)
        {
            Log.i("showfps", fps+"");
            fps = 0;
            mil = 0;
        }
        else {
            mil += elapsed;
            fps++;
        }
    }
    // ### КОНЕЦ счетчика FPS ###

    /*  возращает число полученных очков в этой сессии
     *  вызывается из активити, перед завершением потока
     */
    public int getPoints(){
        return this.points;
    }

    /*  возращает номер раунда в этой сессии
     *  вызывается из активити, перед завершением потока
     */
    public int getRound(){
        return this.round.num;
    }

    /*  устанавливает скорость мяча в зависимости от заданной "силы"
     *  скорость = (координата Х стрелки "силы" - мин. возможная коорд. стрелки "силы") / длина шкалы "силы" * коэф. увеличения скорости
     */
    private final void setBallSpeed()
    {
        float distance = vals.FORCE_ARROW_END_X - vals.FORCE_ARROW_START_X;     // длина шкалы "силы"
        storage.ball.speed = vals.BALL_MIN_SPEED + ((storage.forceArrowX-vals.FORCE_ARROW_START_X)/distance*vals.BALL_PLUS_SPEED);
    }

    /*  связывается с MainActivity для изменения содержимого View-компонентов
     *  принимает тип сообщения:
     *  1 - обновить все view-компоненты
     *  2 - обновить view с кол-вом голов
     *  3 - обновить view с очками
     *  4 - обновить view с временем
     *  5 - исп. метод sendTextMessage: текст по центру
     */
    private final void sendMessage(int type)
    {
        Message msg = new Message();
        switch(type)
        {
            case 1:
                msg = h.obtainMessage(type, new int[]
                        {
                            this.round.num,
                            this.goals,
                            this.points,
                            this.round.time,
                            this.round.maxGoals
                        }
                );
                break;
            case 2:
                msg = h.obtainMessage(type, this.goals, this.round.maxGoals);
                break;
            case 3:
                msg = h.obtainMessage(type, this.points, 0);
                break;
            case 4:
                msg = h.obtainMessage(type, this.round.time, 0);
                break;
        }
        h.sendMessage(msg);
    }

    /*
     *  связывается с MainActivity для изменения текста по центру
     *  параметры:
     *  - содержание сообщения: гол, промах, штанга и пр.
     *  - само сообщение
     *  - длительность сообщения
     *  посылает тип сообщения: 5
     */
    private final void sendTextMessage(int textType, int data, int duration)
    {
        Message msg = h.obtainMessage(5, textType, data);
        h.sendMessage(msg);
        this.showTextTimer = duration;
        this.showText = true;
    }

    /*
     *  проверяет и обновляет параметры раунда
     *  параметры:
     *  - время, вычитается из времени рануда
     */
    private final void checkRoundParams(long time)
    {
        if (this.goals == this.round.maxGoals)  // если забито нужное кол-во голов
        {
            this.goals = 0;
            this.round = new Round(this.round.num+1);
            sendMessage(1);
            sendTextMessage(1, this.round.num, 2000);
        }
        if (this.round.time <= 0)               // если время вышло
        {
            this.step1 = false;
            this.step2 = false;
            this.finished = true;
            this.controlLock = false;
            //this.ballMove = false;
            sendTextMessage(6, 0, 3000);
        }
        else                                    // иначе вычесть время
        {
            this.round.time -= time;
        }
    }

    /*
     *  этот метод вызывется классом MainActivity при нажатии
     *  таким образом, этот поток реагирует на нажатия
     */
    public final void control()
    {
        if (controlLock)    // именно здесь нужна переменная controlLock: это проверка для уменьшения багов
        {
            if (this.step1) // меняем игровые стадии при нажатии
            {
                this.step1 = false;
                this.step2 = true;
                storage.ball.setAngle(storage.arrowAngle);
            }
            else if (this.step2)
            {
                this.step2 = false;
                this.ballMove = true;
                controlLock = false;
                setBallSpeed();
            }
        }
    }

    /*
     *  самый главный метод класса
     *  проверяет и изменяет состояния и свойства игровых объектов
     *  параметры:
     *  - время, так как цикл связан с временем
     */
    private final void update(long time)
    {
        // улучшаем читабельность кода: создаем ссылки
        Ball ball = storage.ball;
        GatePlayer goal = storage.goalkepper;

        // пока мяч и вратарь не пройдут нужное растояние (и скорость и не станет 0)
        if (!controlLock &&
                ball.speed == 0 &&
                goal.distance == 0 && !this.finished)
        {
            // TODO: не правильно работает определние недолета, перелета и штанги
            if (!storage.gates.isGoal)      // если гол не забит
            {
                if (goal.reflected)         // если мяч был отражен
                {
                    this.points -= 150;
                    sendTextMessage(5, -100, 2000);
                }
                else if (storage.gates.isRod)   // если штанга
                {
                    this.points -= 100;
                    sendTextMessage(7, -150, 2000);
                }
                else
                {
                    // TODO: необходима булева переменная, отвечающая за то, пресек ли мяч ординату real[3]
                    if (ball.y >= storage.gates.real[3] || storage.gates.isInside(ball.x, ball.y))  // если пересечена граница real[3]: т. е. перелет
                    {
                        this.points -= 200;
                        sendTextMessage(4, -200, 2000);
                    }
                    // TODO: увеличитить вычитающиеся очки при недолете
                    else        // иначе - недолет
                    {
                        this.points -= 250;
                        sendTextMessage(3, -200, 2000);
                    }
                }
                sendMessage(3);     // обновить очки
            }
            controlLock = true;
            this.step1 = true;
            this.ballMove = false;
            graphics.mRotate.postTranslate(vals.BALL_CENTER_X-storage.ball.x,   // перемещаем мяч в центр
                    vals.BALL_CENTER_Y-storage.ball.y);
            ball.setCoords(vals.BALL_CENTER_X, vals.BALL_CENTER_Y);                 // сохраняем координаты мяча

            graphics.mPlayer1.postTranslate(goal.CENTER_X-goal.x, 0);       // перемещаем игрока
            goal.setCoords(goal.CENTER_X);                                          // сохраняем его координаты
            goal.setRandomParams();                                                 // устанавливаем новые рандомные значения для вратаря
            // сбрасываем параметры
            storage.gates.isGoal = false;
            goal.reflected = false;
        }

        if (showText)   // если нужно показывать текст
        {
            if (this.showTextTimer > 0)
                this.showTextTimer -= time;
            else
            {
                showText = false;
                h.sendMessage(h.obtainMessage(5,0,0));  // очистить текст по центру
            }
        }

        // просчет параметров мяча и вратаря
        if (this.ballMove)
        {
            // устанавливаем смещение мяча и новые координаты
            float shiftX = ball.sX * ball.speed * time;
            float shiftY = ball.sY * ball.speed * time;
            float newX = ball.x + shiftX;
            float newY = ball.y + shiftY;

            if (ball.speed > 0)
                ball.speed -= 0.0002f * time;   // уменьшаем скорость мяча
            else if (ball.speed < 0)
                ball.speed = 0;

            // столкновения с границами экрана и воротами
            if (storage.gates.collide(ball, shiftX, shiftY, graphics.mRotate) && !this.finished)    // если мяч задел ворота изнутри
            {
                this.goals++;           // ГООООЛ!
                sendMessage(2);
                sendTextMessage(2, 200, 2000);
                this.points += 200;     // засчитываем очки
                sendMessage(3);
                this.totalGoals++;      // прибавляем всего голов
            }
            else if (ball.x - ball.radius + shiftX <= 0) {
                ball.sX = -ball.sX;
                newX = ball.radius+1;
                shiftX = newX - ball.x;
            }
            else if (ball.x + ball.radius + shiftX >= width){
                ball.sX = -ball.sX;
                newX = width-ball.radius-1;
                shiftX = newX - ball.x;
            }
            else if (ball.y - ball.radius + shiftY <= 0) {
                ball.sY = -ball.sY;
                newY = ball.radius+1;
                shiftY = newY - ball.y;
            }
            else if (ball.y + ball.radius + shiftY >= height) {
                ball.sY = -ball.sY;
                newY = height-ball.radius-1;
                shiftY =  newY - ball.y;
            }

            // TODO: нормальные столкновения с углами ворот
            // TODO: переписать подобные методы по принципу метода Gates.collide()
            // TODO: storage.gates.cornerCollide(ball, shiftX, shiftY);
            goal.checkCollisions(ball, shiftX, shiftY);     // проверяем столкновения с вратарем

            // перемещаем мяч и запоминаем координаты

            // TODO: нормальная штанга
            //storage.gates.rodCollide(ball, shiftX, shiftY, graphics.mRotate);     // проверяем штангу

            graphics.mRotate.postRotate(ball.speed * 1.2f * time, ball.x, ball.y);  // вращаем мяч вокруг своей оси: скорость вращения зависит от скорости мяча

            // работа с экземпляром класса GatePlayer
            float shift = goal.MAX_SPEED_PER_MS * time;

            if (goal.distance > 0)
            {
                goal.distance -= shift;
                if (goal.left)
                    shift = -shift;
                graphics.mPlayer1.postTranslate(shift, 0);
                goal.setCoords(goal.x + shift);
            }
            else if (goal.distance < 0)
            {
                goal.distance = 0;
            }
        }

        // код для определния угла для стрелки
        if (this.step1)
        {
            float rotation = vals.ROTATION_PER_MS * time;
            if (storage.arrowAngle <= 230f)
                storage.leftRot = false;
            else if (storage.arrowAngle >= 310f)
                storage.leftRot = true;

            if (storage.leftRot)
                rotation = -rotation;

            storage.arrowAngle += rotation;

            graphics.mArrow.postRotate(rotation, vals.BALL_CENTER_X, vals.BALL_CENTER_Y);
        }

        // код для шкалы силы
        if (this.step2)
        {
            float arrowShift = vals.FORCE_ARROW_SPEED*time;
            if (storage.forceArrowX <= vals.FORCE_ARROW_START_X)
                storage.forceArrowLeft = false;
            else if (storage.forceArrowX+graphics.forceArrow.getWidth() >= vals.FORCE_ARROW_END_X)
                storage.forceArrowLeft = true;

            if (storage.forceArrowLeft)
                arrowShift = -arrowShift;

            storage.forceArrowX += arrowShift;

            graphics.mForceArrow.postTranslate(arrowShift, 0);
        }

        // проверяем параметры рануда и обновляем время
        if (!this.finished) {
            checkRoundParams(time);
            sendMessage(4);
        }
    }

    /*
     *  рисует то, что нужно на canvas
     *  параметры:
     *  - ссылка на Canvas, на котором нужно рисовать
     */
    private final void draw(Canvas cnv)
    {
        if(cnv != null)
        {
            cnv.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);                            // очищаем экран
            cnv.drawBitmap(graphics.field, 0,0,  graphics.paint);                     // рисуем: поле,
            cnv.drawBitmap(graphics.ball, graphics.mRotate, graphics.paint);                    // мяч,
            cnv.drawBitmap(graphics.player1, graphics.mPlayer1, graphics.paint);                // вратаря,
            cnv.drawBitmap(graphics.gates, storage.gates.x, storage.gates.y, graphics.paint);   // ворота


            if (this.step1)
                cnv.drawBitmap(graphics.arrow, graphics.mArrow, graphics.paint);                // крансую стрелку

            if (this.step2)
            {
                cnv.drawBitmap(graphics.forceBar, vals.FORCE_BAR_X, vals.FORCE_BAR_Y, graphics.paint);  // шкалу и стрелку силы
                cnv.drawBitmap(graphics.forceArrow, graphics.mForceArrow, graphics.paint);
            }

            // ### КОД ДЛЯ ОТЛАДКИ ###
            // рисует крайние для стрелки силы точки
            /*cnv.drawCircle(vals.FORCE_ARROW_START_X, vals.FORCE_ARROW_Y, 3, graphics.pointShow);
            cnv.drawCircle(vals.FORCE_ARROW_END_X, vals.FORCE_ARROW_Y, 3, graphics.pointShow);*/

            // рисует хит-бокс ворот
            /*cnv.drawRect(storage.gates.real[0], storage.gates.real[1],
                    storage.gates.real[2], storage.gates.real[3], graphics.hitboxShow);

            // рисеут центр мяча
            cnv.drawCircle(storage.ball.x, storage.ball.y, 2f, graphics.pointShow);

            // рисует хит-бокс вратаря
            cnv.drawRect(storage.goalkepper.c[0], storage.goalkepper.c[1],
                    storage.goalkepper.c[2], storage.goalkepper.c[3], graphics.hitboxShow);*/
            // ### КОНЕЦ КОДА для отладки ###
        }
    }

}
