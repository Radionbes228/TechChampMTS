package result;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
// TODO: не изменяй пока не будешь в нормальном состоянии


public class Robot2Rresult {
    static int sideWorldReversSide = 0;

    static int xAbscissa = 15;
    static int yOrdinate = 0;

    static int xAbscissaParent = 15;
    static int yOrdinateParent = 0;

    static int delayDefault = 20;
    static int delay = 40;

    static Set<FinishCell> finishMatrix = new HashSet<>();
    static {
        finishMatrix.add(new FinishCell(7, 7));
        finishMatrix.add(new FinishCell(8, 8));
        finishMatrix.add(new FinishCell(7, 8));
        finishMatrix.add(new FinishCell(8, 7));
    }

    static List<Action> stepToFinish = new LinkedList<>();
    static List<Cell> stopList = new ArrayList<>();

    static boolean isFindFinish = false;


    static List<Cell> path = new LinkedList<>();

    static ConnectRobot connectRobot = new ConnectRobot();
    static SensorData sensorData;

    public static void main(String[] args) throws java.lang.Exception{
        Cell cell = new Cell();
        cell.parent = null;
        cell.XAbscissa = xAbscissa;
        cell.YOrdinate = yOrdinate;

        sensorData = ConnectSensorData.getSensorData();
        int actualType = isActualCellType(sensorData);

        if (actualType == 12) {
            cell.action.push(Action.FORWARD);
            cell.action.push(Action.RIGHT);
            cell.action.push(Action.RIGHT);
            sideWorldReversSide = (sideWorldReversSide + 2) % 4;
            connectRobot.right();
            connectRobot.right();
            connectRobot.forward();
        } else if (actualType == 0 || (actualType >= 2 && actualType <= 4) || actualType == 7) {
            cell.action.push(Action.FORWARD);
            cell.action.push(Action.LEFT);
            sideWorldReversSide = (sideWorldReversSide - 1 + 4) % 4;
            connectRobot.left();
            connectRobot.forward();
        } else if (actualType == 14 || actualType == 1 || actualType == 9 || actualType == 5) {
            cell.action.push(Action.FORWARD);
            connectRobot.forward();
        } else if (actualType == 8 || actualType == 13) {
            cell.action.push(Action.FORWARD);
            cell.action.push(Action.RIGHT);
            sideWorldReversSide = (sideWorldReversSide + 1) % 4;
            connectRobot.right();
            connectRobot.forward();
        }
        setPosition();
        path.add(cell);
        xAbscissaParent = cell.XAbscissa;
        yOrdinateParent = cell.YOrdinate;



        while (!isFindFinish) {
            if (isWas(xAbscissa, yOrdinate)){
                sensorData = ConnectSensorData.getSensorData();
                int actualCellType = isActualCellType(sensorData);

                if (actualCellType == 12) {
                    sideWorldReversSide = (sideWorldReversSide + 2) % 4;
                    connectRobot.right();
                    connectRobot.right();
                    connectRobot.forward();
                } else if (actualCellType == 0 || (actualCellType >= 2 && actualCellType <= 4) || actualCellType == 7) {
                    sideWorldReversSide = (sideWorldReversSide - 1 + 4) % 4;
                    connectRobot.left();
                    connectRobot.forward();
                } else if (actualCellType == 14 || actualCellType == 1 || actualCellType == 9 || actualCellType == 5) {
                    connectRobot.forward();
                } else if (actualCellType == 8 || actualCellType == 13) {
                    sideWorldReversSide = (sideWorldReversSide + 1) % 4;
                    connectRobot.right();
                    connectRobot.forward();
                }
                xAbscissaParent = xAbscissa;
                yOrdinateParent = yOrdinate;
                setPosition();
            }
            else moveRobot();
        }
        moveRobotToFinish();
    }

    static public void moveRobot() throws IOException, InterruptedException {
        sensorData = ConnectSensorData.getSensorData();
        int actualType = isActualCellType(sensorData);
        Cell cell = new Cell();
        cell.XAbscissa = xAbscissa;
        cell.YOrdinate = yOrdinate;
        if (actualType == 12) {
            sideWorldReversSide = (sideWorldReversSide + 2) % 4;
            cell.action.push(Action.FORWARD);
            cell.action.push(Action.RIGHT);
            cell.action.push(Action.RIGHT);
            cell.isDeadEnd = true;
            connectRobot.right();
            connectRobot.right();
            connectRobot.forward();
        } else if (actualType == 0 || (actualType >= 2 && actualType <= 4) || actualType == 7) {
            if (isFork(actualType)) cell.isFork = true;
            cell.action.push(Action.FORWARD);
            cell.action.push(Action.LEFT);
            sideWorldReversSide = (sideWorldReversSide - 1 + 4) % 4;
            connectRobot.left();
            connectRobot.forward();
        } else if (actualType == 14 || actualType == 1 || actualType == 9 || actualType == 5) {
            if (isFork(actualType)) cell.isFork = true;
            cell.action.push(Action.FORWARD);
            connectRobot.forward();
        } else if (actualType == 8 || actualType == 13) {
            cell.action.push(Action.FORWARD);
            cell.action.push(Action.RIGHT);
            sideWorldReversSide = (sideWorldReversSide + 1) % 4;
            connectRobot.right();
            connectRobot.forward();
        }
        setParent(cell);
        setPosition();
        path.add(cell);
        xAbscissaParent = cell.XAbscissa;
        yOrdinateParent = cell.YOrdinate;
        isFinish();
    }

    public static boolean isWas(int x, int y){
        return path.stream().anyMatch(cell -> cell.XAbscissa == x && cell.YOrdinate == y);
    }

    public static boolean isFork(int actualType){
        return actualType >= 0 && actualType <= 3;
    }

    public static void isFinish() throws IOException, InterruptedException {
        for (FinishCell cell: finishMatrix){
            if (path.stream().anyMatch(el -> el.XAbscissa == cell.x && el.YOrdinate == cell.y)){
                isFindFinish = true;
                sideWorldReversSide = 0;
                xAbscissa = 15;
                yOrdinate = 0;
                Thread.sleep(3000);
                connectRobot.restart();
                Thread.sleep(2000);
                break;
            }
        }
    }

    public static void setPosition() {
        if (sideWorldReversSide == 0) xAbscissa--;
        else if (sideWorldReversSide == 1) yOrdinate++;
        else if (sideWorldReversSide == 3) yOrdinate--;
        else xAbscissa++;
    }

    public static void moveRobotToFinish() throws IOException, InterruptedException {

        for (Cell cell : path) {
            Stack<Action> step = cell.action;
            stepToFinish.addAll(step);
        }
        for (int i = 0; i < 2; i++) {
            for (Action action: stepToFinish) {
                if (action == Action.FORWARD) {
                    connectRobot.forward();
                    Thread.sleep(delay);
                }
                else if (action == Action.RIGHT) {
                    connectRobot.right();
                    Thread.sleep(delay);
                }
                else if (action == Action.LEFT) {
                    Thread.sleep(delay);
                    connectRobot.left();
                }
            }
            connectRobot.forward();
            Thread.sleep(1000);
            connectRobot.restart();
        }
    }

    public static void setParent(Cell cell) {
        cell.parent = path.stream().filter(el -> el.XAbscissa == xAbscissaParent && el.YOrdinate == yOrdinateParent).findFirst().orElse(null);
    }
    private static int isActualCellType(SensorData sensorData) {
        boolean[] walls = new boolean[4];
        walls[0] = sensorData.front_distance < SensorData.DISTANCE_NEAREST_CELL;
        walls[1] = sensorData.left_side_distance < SensorData.DISTANCE_NEAREST_CELL;
        walls[2] = sensorData.right_side_distance < SensorData.DISTANCE_NEAREST_CELL;
        walls[3] = sensorData.back_distance < SensorData.DISTANCE_NEAREST_CELL;

        int type = 0;
        if (walls[0] && walls[1] && walls[2] && walls[3]) type = 15;
        else if (walls[1] && walls[2] && walls[3]) type = 14;
        else if (walls[0] && walls[1] && walls[3]) type = 13;
        else if (walls[0] && walls[1] && walls[2]) type = 12;
        else if (walls[0] && walls[2] && walls[3]) type = 11;
        else if (walls[0] && walls[3]) type = 10;
        else if (walls[1] && walls[2]) type = 9;
        else if (walls[0] && walls[1]) type = 8;
        else if (walls[0] && walls[2]) type = 7;
        else if (walls[2] && walls[3]) type = 6;
        else if (walls[1] && walls[3]) type = 5;
        else if (walls[3]) type = 4;
        else if (walls[2]) type = 3;
        else if (walls[0]) type = 2;
        else if (walls[1]) type = 1;

        return type;
    }

    public static class ConnectRobot {
        private final static String TOKEN = "a6351cec-6f32-4e4d-a802-69e46add4b55f3066094-5f04-4619-8870-05455bd3fe1e";
        private final static String URL_ROBOT = "http://127.0.0.1:8801/api/v1";
        private final static String FORWARD = "/robot-cells/forward";
        private final static String RIGHT = "/robot-cells/right";
        private final static String LEFT = "/robot-cells/left";
        private final static String RESTART = "/maze/restart";

        private static final OkHttpClient client;
        static {
            client = new OkHttpClient.Builder()
                    .connectionPool(new ConnectionPool(20, 10, TimeUnit.MINUTES))
                    .build();
        }

        public void right() throws IOException, InterruptedException {
            requestSend(RIGHT);
            Thread.sleep(delayDefault);
        }

        public void left() throws IOException, InterruptedException {
            requestSend(LEFT);
            Thread.sleep(delayDefault);
        }

        public void forward() throws IOException, InterruptedException {
            requestSend(FORWARD);
            Thread.sleep(delayDefault);
        }

        public void restart() throws IOException, InterruptedException {
            requestSend(RESTART);
            Thread.sleep(delayDefault);
        }

        private void requestSend(String action) throws IOException {
            Request request = createRequestMovePOST_notBody(action);
            Response response = client.newCall(request).execute();
            response.close();
        }

        public Request createRequestMovePOST_notBody(String requestEndpoint) {
            return new Request.Builder()
                    .url(URL_ROBOT + requestEndpoint + "?token=" + TOKEN)
                    .post(RequestBody.create(new byte[0]))
                    .build();
        }
    }
    public static class ConnectSensorData {
        private static final String SENSOR_DATA_ENDPOINT = "/robot-cells/sensor-data";
        private static final OkHttpClient client = ConnectRobot.client;
        private static final ObjectMapper objectMapper = new ObjectMapper();

        public static Request createRequestMoveGET(String requestEndpoint) {
            return new Request.Builder()
                    .url(ConnectRobot.URL_ROBOT + requestEndpoint + "?token=" + ConnectRobot.TOKEN)
                    .get()
                    .build();
        }

        public static SensorData getSensorData() throws InterruptedException {
            Thread.sleep(delayDefault);
            try (Response response = client.newCall(createRequestMoveGET(SENSOR_DATA_ENDPOINT)).execute()) {
                assert response.body() != null;
                JsonNode jsonNode = objectMapper.readTree(response.body().string());

                return new SensorData(
                        getSensorValue(jsonNode, "front_distance"),
                        getSensorValue(jsonNode, "right_side_distance"),
                        getSensorValue(jsonNode, "left_side_distance"),
                        getSensorValue(jsonNode, "back_distance"),
                        getSensorValue(jsonNode, "left_45_distance"),
                        getSensorValue(jsonNode, "right_45_distance"),
                        getSensorValue(jsonNode, "rotation_pitch"),
                        getSensorValue(jsonNode, "rotation_yaw"),
                        getSensorValue(jsonNode, "rotation_roll"),
                        getSensorValue(jsonNode, "down_x_offset"),
                        getSensorValue(jsonNode, "down_y_offset")
                );
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        public static Float getSensorValue(JsonNode jsonNode, String param) {
            return jsonNode.get(param).floatValue();
        }
    }
    public record SensorData(float front_distance, float right_side_distance, float left_side_distance,
                             float back_distance, float left_45_distance, float right_45_distance, float rotation_pitch,
                             float rotation_yaw, float rotation_roll, float down_x_offset, float down_y_offset) {
        public static final float DISTANCE_NEAREST_CELL = 65F;
    }
    public static class Cell {
        public int XAbscissa;
        public int YOrdinate;
        public int score = 0;
        public Stack<Action> action = new Stack<>();
        public Cell parent;
        public Cell(){}
        boolean isFork;
        boolean isDeadEnd;
    }
    public static class FinishCell{
        int x;
        int y;

        public FinishCell(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FinishCell that = (FinishCell) o;
            return x == that.x && y == that.y;
        }

        @Override
        public int hashCode() {
            int result = x;
            result = 31 * result + y;
            return result;
        }
    }
    public enum Action{
        FORWARD, RIGHT, LEFT
    }

    /*public static void findPath(Cell start, FinishCell finish, Cell[][] grid) {
        // Создаем очередь приоритета для хранения ячеек, которые нужно обработать
        PriorityQueue<Cell> queue = new PriorityQueue<>();

        // Создаем множество для отслеживания посещенных ячеек
        Set<Cell> visited = new HashSet<>();

        // Инициализируем стартовую ячейку
        start.score = 0;
        start.parent = null;
        queue.add(start);

        while (!queue.isEmpty()) {
            Cell current = queue.poll();

            // Если мы достигли финишной ячейки,构руируем путь
            if (current.XAbscissa == finish.x && current.YOrdinate == finish.y) return;

            // Помечаем текущую ячейку как посещенную
            visited.add(current);


        }
    }

    public static int heuristic(Cell start, Cell finish){
        return Math.abs(start.XAbscissa - finish.XAbscissa) + Math.abs(start.YOrdinate - finish.YOrdinate);
    }*/

}
