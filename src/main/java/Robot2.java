/*
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.util.*;

public class Robot2 {
    static int sideWorldReversSide = 0;
    static int xAbscissa = 15;
    static int yOrdinate = 0;

    static ConnectRobot connectRobot = new ConnectRobot();
    static SensorData sensorData;

    static int sizeMaze = 15;

    static int[][] maze = new int[16][16];
    static {
        for (int i = 0; i < 16; i++) {
            Arrays.fill(maze[i], -1);
        }
    }

    static int endX = 7;
    static int endY = 7;

    public static void main(String[] args) throws Exception{
        while (true) {
            sensorData = ConnectSensorData.getSensorData();
//            int actualType = isActualCellType(sensorData);

            AStar.findPath(maze, xAbscissa, yOrdinate, endX, endY);
        }
    }


    public static void setPosition() {
        if (sideWorldReversSide == 0) xAbscissa--;
        else if (sideWorldReversSide == 1) yOrdinate++;
        else if (sideWorldReversSide == 3) yOrdinate--;
        else xAbscissa++;
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
        private final static String BACKWARD = "/robot-cells/backward";
        private final static String RIGHT = "/robot-cells/right";
        private final static String LEFT = "/robot-cells/left";

        private static final OkHttpClient client = new OkHttpClient();

        public OkHttpClient getOkHttpClient(){
            return client;
        }

        public void right() throws IOException {
            Request request = createRequestMovePOST_notBody(RIGHT);
            Response response = client.newCall(request).execute();
            response.close();
        }

        public void left() throws IOException {
            Request request = createRequestMovePOST_notBody(LEFT);
            Response response = client.newCall(request).execute();
            response.close();
        }

        public void forward() throws IOException {
            Request request = createRequestMovePOST_notBody(FORWARD);
            Response response = client.newCall(request).execute();
            response.close();
        }
        public void backward() throws IOException {
            Request request = createRequestMovePOST_notBody(BACKWARD);
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

        public static SensorData getSensorData() {
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

    public static int[][] getRelativeDirections() {
        int[][] relativeDirections = new int[4][2];
        if (sideWorldReversSide == 0) {
            relativeDirections[0] = new int[]{0, -1}; // left
            relativeDirections[1] = new int[]{-1, 0}; // up
            relativeDirections[2] = new int[]{0, 1}; // right
            relativeDirections[3] = new int[]{1, 0}; // down
        } else if (sideWorldReversSide == 1) { // robot is facing right
            relativeDirections[0] = new int[]{-1, 0}; // up
            relativeDirections[1] = new int[]{0, 1}; // right
            relativeDirections[2] = new int[]{1, 0}; // down
            relativeDirections[3] = new int[]{0, -1}; // left
        } else if (sideWorldReversSide == 3) { // robot is facing down
            relativeDirections[0] = new int[]{1, 0}; // down
            relativeDirections[1] = new int[]{0, -1}; // left
            relativeDirections[2] = new int[]{-1, 0}; // up
            relativeDirections[3] = new int[]{0, 1}; // right
        } else if (sideWorldReversSide == 2) { // robot is facing left
            relativeDirections[1] = new int[]{0, 1}; // right
            relativeDirections[2] = new int[]{1, 0}; // down
            relativeDirections[3] = new int[]{0, -1}; // left
            relativeDirections[0] = new int[]{-1, 0}; // up
        }
        return relativeDirections;
    }
    public static void siOpen(int actualType, int newX, int newY, int xDir, int yDir) {
        switch (sideWorldReversSide){
            case 0:{
                if ((actualType == 0 || actualType == 2 || actualType == 3 || actualType == 7) && (xDir == 0 && yDir == -1)) {
                    maze[newX][newY] = 0;
                }else if ((actualType == 0 || actualType == 1 || actualType == 3 || actualType == 9 || actualType == 14) && (xDir == -1 && yDir == 0)){
                    maze[newX][newY] = 0;
                }else if (((actualType >= 0 && actualType <= 2) || actualType == 8 || actualType == 13) && (xDir == 0 && yDir == 1)){
                    maze[newX][newY] = 0;
                }else if (actualType == 12 && (xDir == 1 && yDir == 0)){
                    maze[newX][newY] = 0;
                }
                break;
            }
            case 1:{
                if ((actualType == 0 || actualType == 2 || actualType == 3 || actualType == 7) && (xDir == -1 && yDir == 0)) {
                    maze[newX][newY] = 0;
                }else if ((actualType == 0 || actualType == 1 || actualType == 3 || actualType == 9 || actualType == 14) && (xDir == 0 && yDir == 1)){
                    maze[newX][newY] = 0;
                }else if (((actualType >= 0 && actualType <= 2) || actualType == 8 || actualType == 13) && (xDir == 1 && yDir == 0)){
                    maze[newX][newY] = 0;
                }else if (actualType == 12 && (xDir == 0 && yDir == -1)){
                    maze[newX][newY] = 0;
                }
                break;
            }
            case 2:{
                if ((actualType == 0 || actualType == 2 || actualType == 3 || actualType == 7) && (xDir == 0 && yDir == 1)) {
                    maze[newX][newY] = 0;
                }else if ((actualType == 0 || actualType == 1 || actualType == 3 || actualType == 9 || actualType == 14) && (xDir == 1 && yDir == 0)){
                    maze[newX][newY] = 0;
                }else if (((actualType >= 0 && actualType <= 2) || actualType == 8 || actualType == 13) && (xDir == 0 && yDir == -1)){
                    maze[newX][newY] = 0;
                }else if (actualType == 12 && (xDir == -1 && yDir == 0)){
                    maze[newX][newY] = 0;
                }
                break;
            }
            case 3:{
                if ((actualType == 0 || actualType == 2 || actualType == 3 || actualType == 7) && (xDir == 1 && yDir == 0)) {
                    maze[newX][newY] = 0;
                }else if ((actualType == 0 || actualType == 1 || actualType == 3 || actualType == 9 || actualType == 14) && (xDir == 0 && yDir == -1)){
                    maze[newX][newY] = 0;
                }else if (((actualType >= 0 && actualType <= 2) || actualType == 8 || actualType == 13) && (xDir == -1 && yDir == 0)){
                    maze[newX][newY] = 0;
                }else if (actualType == 12 && (xDir == 0 && yDir == 1)){
                    maze[newX][newY] = 0;
                }
                break;
            }
        }
    }
    public static AStar.Node getNode(int newX, int newY, int xDir, int yDir, AStar.Node currentNode) {
        return switch (sideWorldReversSide) {
            case 0 -> {
                if (xDir == 0 && yDir == -1)
                    yield new AStar.Node(newX, newY, currentNode.cost + 1, currentNode, Action.LEFT);
                else if (xDir == -1 && yDir == 0)
                    yield new AStar.Node(newX, newY, currentNode.cost + 1, currentNode, Action.FORWARD);
                else if (xDir == 0 && yDir == 1)
                    yield new AStar.Node(newX, newY, currentNode.cost + 1, currentNode, Action.RIGHT);
                else yield new  AStar.Node(newX, newY, currentNode.cost + 1, currentNode, Action.REVERS);
            }
            case 1 -> {
                if (xDir == -1 && yDir == 0)
                    yield  new AStar.Node(newX, newY, currentNode.cost + 1, currentNode, Action.LEFT);
                else if (xDir == 0 && yDir == 1)
                    yield new AStar.Node(newX, newY, currentNode.cost + 1, currentNode, Action.FORWARD);
                else if (xDir == 1 && yDir == 0)
                    yield new AStar.Node(newX, newY, currentNode.cost + 1, currentNode, Action.RIGHT);
                else yield new  AStar.Node(newX, newY, currentNode.cost + 1, currentNode, Action.REVERS);
            }
            case 2 -> {
                if (xDir == 0 && yDir == 1)
                    yield new AStar.Node(newX, newY, currentNode.cost + 1, currentNode, Action.LEFT);
                else if (xDir == 1 && yDir == 0)
                    yield new AStar.Node(newX, newY, currentNode.cost + 1, currentNode, Action.FORWARD);
                else if (xDir == 0 && yDir == -1)
                    yield new AStar.Node(newX, newY, currentNode.cost + 1, currentNode, Action.RIGHT);
                else yield new  AStar.Node(newX, newY, currentNode.cost + 1, currentNode, Action.REVERS);
            }
            case 3 -> {
                if (xDir == 1 && yDir == 0)
                    yield new AStar.Node(newX, newY, currentNode.cost + 1, currentNode, Action.LEFT);
                else if (xDir == 0 && yDir == -1)
                    yield new AStar.Node(newX, newY, currentNode.cost + 1, currentNode, Action.FORWARD);
                else if (xDir == -1 && yDir == 0)
                    yield new AStar.Node(newX, newY, currentNode.cost + 1, currentNode, Action.RIGHT);
                else yield new  AStar.Node(newX, newY, currentNode.cost + 1, currentNode, Action.REVERS);
            }
            default -> throw new IllegalStateException("Unexpected value: " + sideWorldReversSide);
        };
    }


    static class AStar {
        static PriorityQueue<Node> openList = new PriorityQueue<>(Comparator.comparingDouble(n -> n.cost + n.heuristic));
        static Set<Node> closedList = new HashSet<>();;

        public static void findPath(int[][] maze, int startX, int startY, int endX, int endY) throws IOException {
            final int[][] DIRECTIONS = getRelativeDirections(); // направления движения (вправо, вниз, влево, вверх)
            Node startNode = new Node(startX, startY, 0, null, null);
            Node endNode = new Node(endX, endY, 0, null, null);
            int actualType = isActualCellType(sensorData);

            openList.add(startNode);

            Node currentNode = openList.poll();
            closedList.add(currentNode);

            assert currentNode != null;
            if (currentNode.y == endNode.y && currentNode.x == endNode.x) {
                reconstructPath(currentNode);
                return;
            }

            for (int[] direction : DIRECTIONS) {
                int xDir = direction[0];
                int yDir = direction[1];
                int newX = currentNode.x + xDir;
                int newY = currentNode.y + yDir;
                if (newX > sizeMaze || newY > sizeMaze ||  newY < 0 || newX < 0) continue;

                siOpen(actualType, newX, newY, xDir, yDir);

                if (newX >= 0  && newY >= 0  && maze[newX][newY] == 0) {
                    Node neighbor = getNode(newX, newY, xDir, yDir, currentNode);

                    if (closedList.stream().anyMatch(el -> el.x == neighbor.x && el.y == neighbor.y)) continue;

                    neighbor.heuristic = heuristic(neighbor, endNode);

                    if (!openList.contains(neighbor)) {
                        openList.add(neighbor);
                    } else {
                        for (Node openNode : openList) {
                            if (openNode.equals(neighbor) && openNode.cost > neighbor.cost) {
                                openList.remove(openNode);
                                openList.add(neighbor);
                                break;
                            }
                        }
                    }
                }
            }

            Node nextCell = openList.poll();
            assert nextCell != null;
            if (nextCell.action == Action.FORWARD){
                connectRobot.forward();
                setPosition();
                reconstructPath(currentNode);
                closedList.add(nextCell);
                return;
            } else if (nextCell.action == Action.REVERS) {
                connectRobot.right();
                connectRobot.right();
                connectRobot.forward();
                setPosition();
                reconstructPath(currentNode);

                closedList.add(nextCell);

                return;

            } else if (nextCell.action == Action.RIGHT){
                sideWorldReversSide = (sideWorldReversSide + 1) % 4;
                connectRobot.right();
                connectRobot.forward();
                setPosition();
                reconstructPath(currentNode);

                closedList.add(nextCell);
                return;
            } else if (nextCell.action == Action.LEFT){
                sideWorldReversSide = (sideWorldReversSide - 1 + 4) % 4;
                connectRobot.left();
                connectRobot.forward();
                setPosition();
                reconstructPath(currentNode);
                closedList.add(nextCell);
                return;
            }else {
                System.err.println("Нет свободного пути!");
                return;
            }
        }

        private static void reconstructPath(Node node) {
            List<Node> path = new ArrayList<>();
            while (node != null) {
                path.add(node);
                node = node.parent;
            }
            Collections.reverse(path);
            path.forEach(el -> System.out.println(el.x + "  " + el.y));
            System.out.println("-----------------------");
        }

        private static float heuristic(Node node, Node goal) {
            return Math.abs(node.x - goal.x) + Math.abs(node.y - goal.y);
        }

        public static class Node {
            int x, y;
            float cost;
            float heuristic;
            Node parent;
            Action action;

            public Node(int x, int y, float cost, Node parent, Action action) {
                this.x = x;
                this.y = y;
                this.cost = cost;
                this.parent = parent;
                this.action = action;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;

                Node node = (Node) o;
                return x == node.x && y == node.y && Float.compare(cost, node.cost) == 0 && Float.compare(heuristic, node.heuristic) == 0 && Objects.equals(parent, node.parent) && action == node.action;
            }

            @Override
            public int hashCode() {
                int result = x;
                result = 31 * result + y;
                result = 31 * result + Float.hashCode(cost);
                result = 31 * result + Float.hashCode(heuristic);
                result = 31 * result + Objects.hashCode(parent);
                result = 31 * result + Objects.hashCode(action);
                return result;
            }
        }
    }

    public enum Action{
        RIGHT, LEFT, FORWARD, REVERS
    }

}
*/
