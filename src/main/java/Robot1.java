import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Robot1 {
    static int sideWorldReversSide = 0;
    
    public static void main(String[] args) {
        ConnectRobot connectRobot = new ConnectRobot();
        ConnectSensorData connectSensorData = new  ConnectSensorData();
        Cell[] matrix = new Cell[256];

//        int xAbscissa = 15;
//        int yOrdinate = 0;

//        Float offsetX;
//        Float offsetY;



        int oneWallCount = 1;
        int twoParallelWall = 9;
        int twoFusedWall = 5;
        int threeFusedWall = 11;

        SensorData sensorData;
        while (true) {
            sensorData = connectSensorData.getSensorData();
            if (isRevers(sensorData)) {
                System.out.println(getCellType(isCellType(sensorData)));
                if (sideWorldReversSide == 3) sideWorldReversSide = 1;
                else if (sideWorldReversSide == 2) sideWorldReversSide = 0;
                else if (sideWorldReversSide == 1) sideWorldReversSide = 3;
                else if (sideWorldReversSide == 0) sideWorldReversSide = 2;
                connectRobot.right();
                connectRobot.right();
                connectRobot.forward();
            } else if (isLeft(sensorData)) {
                System.out.println(getCellType(isCellType(sensorData)));
                if (sideWorldReversSide <= 3 && sideWorldReversSide >= 1) sideWorldReversSide--;
                else if (sideWorldReversSide == 0) sideWorldReversSide = 3;
                connectRobot.left();
                connectRobot.forward();
            } else if (isForward(sensorData)) {
                System.out.println(getCellType(isCellType(sensorData)));
                connectRobot.forward();
            } else if (isRight(sensorData)) {
                System.out.println(getCellType(isCellType(sensorData)));
                if (sideWorldReversSide >= 0 && sideWorldReversSide <= 2) sideWorldReversSide++;
                else if (sideWorldReversSide == 3) sideWorldReversSide = 0;
                connectRobot.right();
                connectRobot.forward();
            }
        }
    }


    // Почти решенная проблема !!!!!!!! НИ В КОЕМ СЛУЧАЕ НЕ УДАЛАЯТЬ РЕШЕНИЕ !!!!!!!!!! ВСПОМНИ РЕШЕНИЕ ПРИДУРОК!!!
    public static int getCellType(int actualType) {
        if (sideWorldReversSide == 1 || sideWorldReversSide == 3) {
            if (actualType == 9 || actualType == 10) {
                return actualType + 1;
            } else if (actualType >= 11 && actualType <= 14) {
                if (sideWorldReversSide == 1) return actualType - 1;
                else return actualType + 1;
            } else if (actualType >= 5 && actualType <= 8) {
                if (actualType == 7 && sideWorldReversSide == 3) return actualType + 1;
                else if (sideWorldReversSide == 3) return actualType - 3;
                else return actualType - sideWorldReversSide;
            } else if (actualType >= 1 && actualType <= 4) {
                if (sideWorldReversSide == 1) return actualType + 1;
                else if (actualType == 1) return actualType + sideWorldReversSide;
                else return actualType - 1;
            } else return actualType;
        } else if (sideWorldReversSide == 2) {
            if (actualType >= 11 && actualType <= 14) {
                return actualType + sideWorldReversSide;
            } else if (actualType >= 5 && actualType <= 8) {
                return actualType - sideWorldReversSide;
            } else if (actualType >= 1 && actualType <= 4) {
                return actualType + sideWorldReversSide;
            } else return actualType;
        } else return actualType;
    }

/*    public static int getCellType(int actualType) {
        if (sideWorldReversSide == 1 || sideWorldReversSide == 3) {
            if (actualType == 9 || actualType == 10) {
                return actualType + 1;
            } else if (actualType >= 11 && actualType <= 14) {
                if (sideWorldReversSide == 1) return actualType - 1;
                else return actualType + 1;
            } else if (actualType >= 5 && actualType <= 8) {
                if (actualType == 7 && sideWorldReversSide == 3) return actualType + 1;
                else if (sideWorldReversSide == 3) return actualType - 3;
                else return actualType - sideWorldReversSide;
            } else if (actualType >= 1 && actualType <= 4) {
                if (sideWorldReversSide == 1) return actualType + 1;
                else if (actualType == 1) return actualType + sideWorldReversSide;
                else return actualType - 1;
            } else return actualType;
        } else if (sideWorldReversSide == 2) {
            if (actualType >= 11 && actualType <= 14) {
                return actualType + sideWorldReversSide;
            } else if (actualType >= 5 && actualType <= 8) {
                return actualType - sideWorldReversSide;
            } else if (actualType >= 1 && actualType <= 4) {
                return actualType + sideWorldReversSide;
            } else return actualType;
        } else return actualType;
    }*/

    public static int isCellType(SensorData sensorData) {
        if (isWall15(sensorData)) return 15;
        else if (isWall14(sensorData)) return 14;
        else if (isWall13(sensorData)) return 13;
        else if (isWall12(sensorData)) return 12;
        else if (isWall11(sensorData)) return 11;
        else if (isWall10(sensorData)) return 10;
        else if (isWall9(sensorData)) return 9;
        else if (isWall8(sensorData)) return 8;
        else if (isWall7(sensorData)) return 7;
        else if (isWall6(sensorData)) return 6;
        else if (isWall5(sensorData)) return 5;
        else if (isWall4(sensorData)) return 4;
        else if (isWall3(sensorData)) return 3;
        else if (isWall2(sensorData)) return 2;
        else if (isWall1(sensorData)) return 1;
        else return 0;
    }

    //Good
    private static boolean isRevers(SensorData sensorData) {
        return (sensorData.front_distance < SensorData.DISTANCE_NEAREST_CELL) && (sensorData.right_side_distance < SensorData.DISTANCE_NEAREST_CELL) && (sensorData.left_side_distance < SensorData.DISTANCE_NEAREST_CELL);
    }
    private static boolean isRight(SensorData sensorData) {
        return (sensorData.front_distance < SensorData.DISTANCE_OPEN_CELL && sensorData.left_side_distance < SensorData.DISTANCE_OPEN_CELL);
    }
    private static boolean isLeft(SensorData sensorData) {
        if ((sensorData.front_distance > SensorData.DISTANCE_NEAREST_CELL && sensorData.front_distance < SensorData.DISTANCE_OPEN_CELL)
                && (sensorData.left_side_distance > SensorData.DISTANCE_NEAREST_CELL && sensorData.left_side_distance < SensorData.DISTANCE_OPEN_CELL)
                    && (sensorData.right_side_distance > SensorData.DISTANCE_NEAREST_CELL && sensorData.right_side_distance < SensorData.DISTANCE_OPEN_CELL)){
            return true;
        }
        else if ((sensorData.front_distance > SensorData.DISTANCE_NEAREST_CELL && sensorData.front_distance < SensorData.DISTANCE_OPEN_CELL)
                && (sensorData.left_side_distance > SensorData.DISTANCE_NEAREST_CELL && sensorData.left_side_distance < SensorData.DISTANCE_OPEN_CELL)){
            return true;
        } else if ((sensorData.right_side_distance > SensorData.DISTANCE_NEAREST_CELL && sensorData.right_side_distance < SensorData.DISTANCE_OPEN_CELL)
                && (sensorData.left_side_distance > SensorData.DISTANCE_NEAREST_CELL && sensorData.left_side_distance < SensorData.DISTANCE_OPEN_CELL)) {
            return true;
        } else return sensorData.front_distance < SensorData.DISTANCE_NEAREST_CELL && sensorData.right_side_distance < SensorData.DISTANCE_NEAREST_CELL;
    }
    private static boolean isForward(SensorData sensorData) {
        if (sensorData.right_side_distance < SensorData.DISTANCE_NEAREST_CELL && sensorData.left_side_distance < SensorData.DISTANCE_NEAREST_CELL) {
            return true;
        }else return (sensorData.front_distance > SensorData.DISTANCE_NEAREST_CELL && sensorData.front_distance < SensorData.DISTANCE_OPEN_CELL)
                && (sensorData.right_side_distance > SensorData.DISTANCE_NEAREST_CELL && sensorData.right_side_distance < SensorData.DISTANCE_OPEN_CELL);
    }
    // Good
    private static boolean isWall0(SensorData sensorData){
        return (sensorData.right_side_distance < SensorData.DISTANCE_OPEN_CELL && sensorData.right_side_distance < SensorData.DISTANCE_NEAREST_CELL)
                && (sensorData.left_side_distance < SensorData.DISTANCE_OPEN_CELL && sensorData.left_side_distance < SensorData.DISTANCE_NEAREST_CELL)
                && (sensorData.front_distance < SensorData.DISTANCE_OPEN_CELL && sensorData.front_distance < SensorData.DISTANCE_NEAREST_CELL)
                && (sensorData.back_distance < SensorData.DISTANCE_OPEN_CELL && sensorData.back_distance < SensorData.DISTANCE_NEAREST_CELL);
    }
    private static boolean isWall1(SensorData sensorData){
        return (sensorData.left_side_distance < SensorData.DISTANCE_NEAREST_CELL);
    }
    private static boolean isWall2(SensorData sensorData){
        return (sensorData.front_distance < SensorData.DISTANCE_NEAREST_CELL);
    }
    private static boolean isWall3(SensorData sensorData){
        return sensorData.right_side_distance < SensorData.DISTANCE_NEAREST_CELL;
    }
    private static boolean isWall4(SensorData sensorData){
        return sensorData.back_distance < SensorData.DISTANCE_NEAREST_CELL;
    }
    private static boolean isWall5(SensorData sensorData){
        return sensorData.left_side_distance < SensorData.DISTANCE_NEAREST_CELL && sensorData.back_distance < SensorData.DISTANCE_NEAREST_CELL;
    }
    private static boolean isWall6(SensorData sensorData){
        return (sensorData.right_side_distance < SensorData.DISTANCE_NEAREST_CELL && sensorData.back_distance < SensorData.DISTANCE_NEAREST_CELL);
    }
    private static boolean isWall7(SensorData sensorData){
        return sensorData.right_side_distance < SensorData.DISTANCE_NEAREST_CELL && sensorData.front_distance < SensorData.DISTANCE_NEAREST_CELL;
    }
    private static boolean isWall8(SensorData sensorData){
        return sensorData.left_side_distance < SensorData.DISTANCE_NEAREST_CELL && sensorData.front_distance < SensorData.DISTANCE_NEAREST_CELL;


    }
    private static boolean isWall9(SensorData sensorData){
        return sensorData.right_side_distance < SensorData.DISTANCE_NEAREST_CELL && sensorData.left_side_distance < SensorData.DISTANCE_NEAREST_CELL;
    }
    private static boolean isWall10(SensorData sensorData){
        return sensorData.front_distance < SensorData.DISTANCE_NEAREST_CELL && sensorData.back_distance < SensorData.DISTANCE_NEAREST_CELL;
    }
    private static boolean isWall11(SensorData sensorData){
        return sensorData.right_side_distance < SensorData.DISTANCE_NEAREST_CELL
                && sensorData.front_distance < SensorData.DISTANCE_NEAREST_CELL
                && sensorData.back_distance < SensorData.DISTANCE_NEAREST_CELL;
    }
    private static boolean isWall12(SensorData sensorData){
        return sensorData.right_side_distance < SensorData.DISTANCE_NEAREST_CELL
                && sensorData.left_side_distance < SensorData.DISTANCE_NEAREST_CELL
                && sensorData.front_distance < SensorData.DISTANCE_NEAREST_CELL;
    }
    private static boolean isWall13(SensorData sensorData){
        return sensorData.left_side_distance < SensorData.DISTANCE_NEAREST_CELL
                && sensorData.front_distance < SensorData.DISTANCE_NEAREST_CELL
                && sensorData.back_distance < SensorData.DISTANCE_NEAREST_CELL;
    }
    private static boolean isWall14(SensorData sensorData){
        return sensorData.right_side_distance < SensorData.DISTANCE_NEAREST_CELL
                && sensorData.left_side_distance < SensorData.DISTANCE_NEAREST_CELL
                && sensorData.back_distance < SensorData.DISTANCE_NEAREST_CELL;
    }
    private static boolean isWall15(SensorData sensorData){
        return sensorData.right_side_distance < SensorData.DISTANCE_NEAREST_CELL
                && sensorData.left_side_distance < SensorData.DISTANCE_NEAREST_CELL
                && sensorData.front_distance < SensorData.DISTANCE_NEAREST_CELL
                && sensorData.back_distance < SensorData.DISTANCE_NEAREST_CELL;
    }


    public static class ConnectRobot {
        private final static String TOKEN = "a6351cec-6f32-4e4d-a802-69e46add4b55f3066094-5f04-4619-8870-05455bd3fe1e";
        private final static String URL_ROBOT = "http://127.0.0.1:8801/api/v1";
        private final static String FORWARD = "/robot-cells/forward";
        private final static String RIGHT = "/robot-cells/right";
        private final static String LEFT = "/robot-cells/left";
        private final static String BACKWARD = "/robot-cells/backward";

        public HttpClient getHttpClient() {
            return HttpClient.newHttpClient();
        }

        public HttpStatus right() {
            HttpClient client = getHttpClient();
            HttpRequest request = createRequestMovePOST_notBody(RIGHT);
            try {
                sendRequest_notResponseBody(client, request);
            } catch (IOException | InterruptedException e) {
                return HttpStatus.VALID_ERROR;
            }
            return HttpStatus.OK;
        }

        public HttpStatus left() {
            HttpClient client = getHttpClient();
            HttpRequest request = createRequestMovePOST_notBody(LEFT);
            try {
                sendRequest_notResponseBody(client, request);
            } catch (IOException | InterruptedException e) {
                return HttpStatus.VALID_ERROR;
            }
            return HttpStatus.OK;
        }

        public HttpStatus forward() {
            HttpClient client = getHttpClient();
            HttpRequest request = createRequestMovePOST_notBody(FORWARD);
            try {
                sendRequest_notResponseBody(client, request);
            } catch (IOException | InterruptedException e) {
                return HttpStatus.VALID_ERROR;
            }
            return HttpStatus.OK;
        }

        public HttpStatus backward() {
            HttpClient client = getHttpClient();
            HttpRequest request = createRequestMovePOST_notBody(BACKWARD);
            try {
                sendRequest_notResponseBody(client, request);
            } catch (IOException | InterruptedException e) {
                return HttpStatus.VALID_ERROR;
            }
            return HttpStatus.OK;
        }

        public HttpRequest createRequestMovePOST_notBody(String requestEndpoint) {
            return HttpRequest.newBuilder()
                    .uri(URI.create(URL_ROBOT + requestEndpoint + "?token=" + TOKEN))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();
        }

        public void sendRequest_notResponseBody(HttpClient client, HttpRequest request) throws IOException, InterruptedException {
            client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        public HttpResponse<String> sendRequest(HttpClient client, HttpRequest request) throws IOException, InterruptedException {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        }
    }
    public static class ConnectSensorData{
        private static final String SENSOR_DATA_ENDPOINT = "/robot-cells/sensor-data";
        private static final ConnectRobot connectRobot = new ConnectRobot();

        public SensorData getSensorData(){
            HttpClient client = connectRobot.getHttpClient();
            HttpRequest request = createRequestMoveGET(SENSOR_DATA_ENDPOINT);
            HttpResponse<String> response;
            try {
                response = connectRobot.sendRequest(client, request);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e.getMessage());
            }

            return new SensorData(
                    getSensorValue(response, "front_distance"),
                    getSensorValue(response, "right_side_distance"),
                    getSensorValue(response, "left_side_distance"),
                    getSensorValue(response, "back_distance"),
                    getSensorValue(response, "left_45_distance"),
                    getSensorValue(response, "right_45_distance"),
                    getSensorValue(response, "rotation_pitch"),
                    getSensorValue(response, "rotation_yaw"),
                    getSensorValue(response, "rotation_roll"),
                    getSensorValue(response, "down_x_offset"),
                    getSensorValue(response, "down_y_offset")
            );
        }

        public HttpRequest createRequestMoveGET(String requestEndpoint) {
            return HttpRequest.newBuilder()
                    .uri(URI.create(ConnectRobot.URL_ROBOT + requestEndpoint + "?token=" + ConnectRobot.TOKEN))
                    .GET()
                    .build();
        }

        public Float getSensorValue(HttpResponse<String> response, String param) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = null;
            try {
                jsonNode = objectMapper.readTree(response.body());
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            return jsonNode.get(param).floatValue();
        }
    }
    public static final class SensorData {
        private static final Float DISTANCE_NEAREST_CELL = 65F;
        private static final Float DISTANCE_OPEN_CELL = 2000F;
        private static final Float POSITION_RELATIVE_NORTH = 3F;
        private static final Float POSITION_RELATIVE_SOUTH = 3F;
        private static final Float POSITION_RELATIVE_WEST = 3F;
        private static final Float POSITION_RELATIVE_EAST = 3F;

        private final Float front_distance;
        private final Float right_side_distance;
        private final Float left_side_distance;
        private final Float back_distance;
        private final Float left_45_distance;
        private final Float right_45_distance;
        private final Float rotation_pitch;
        private final Float rotation_yaw;
        private final Float rotation_roll;
        private Float down_x_offset;
        private Float down_y_offset;

        public SensorData(Float front_distance, Float right_side_distance, Float left_side_distance,
                          Float back_distance, Float left_45_distance, Float right_45_distance, Float rotation_pitch,
                          Float rotation_yaw, Float rotation_roll, Float down_x_offset, Float down_y_offset) {
            this.front_distance = front_distance;
            this.right_side_distance = right_side_distance;
            this.left_side_distance = left_side_distance;
            this.back_distance = back_distance;
            this.left_45_distance = left_45_distance;
            this.right_45_distance = right_45_distance;
            this.rotation_pitch = rotation_pitch;
            this.rotation_yaw = rotation_yaw;
            this.rotation_roll = rotation_roll;
            this.down_x_offset = down_x_offset;
            this.down_y_offset = down_y_offset;
        }
/*
        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (SensorData) obj;
            return Objects.equals(this.front_distance, that.front_distance) &&
                    Objects.equals(this.right_side_distance, that.right_side_distance) &&
                    Objects.equals(this.left_side_distance, that.left_side_distance) &&
                    Objects.equals(this.back_distance, that.back_distance) &&
                    Objects.equals(this.left_45_distance, that.left_45_distance) &&
                    Objects.equals(this.right_45_distance, that.right_45_distance) &&
                    Objects.equals(this.rotation_pitch, that.rotation_pitch) &&
                    Objects.equals(this.rotation_yaw, that.rotation_yaw) &&
                    Objects.equals(this.rotation_roll, that.rotation_roll) &&
                    Objects.equals(this.down_x_offset, that.down_x_offset) &&
                    Objects.equals(this.down_y_offset, that.down_y_offset);
        }

        @Override
        public int hashCode() {
            return Objects.hash(front_distance, right_side_distance, left_side_distance, back_distance, left_45_distance, right_45_distance, rotation_pitch, rotation_yaw, rotation_roll, down_x_offset, down_y_offset);
        }

        @Override
        public String toString() {
            return "SensorData[" +
                    "front_distance=" + front_distance + ", " +
                    "right_side_distance=" + right_side_distance + ", " +
                    "left_side_distance=" + left_side_distance + ", " +
                    "back_distance=" + back_distance + ", " +
                    "left_45_distance=" + left_45_distance + ", " +
                    "right_45_distance=" + right_45_distance + ", " +
                    "rotation_pitch=" + rotation_pitch + ", " +
                    "rotation_yaw=" + rotation_yaw + ", " +
                    "rotation_roll=" + rotation_roll + ", " +
                    "down_x_offset=" + down_x_offset + ", " +
                    "down_y_offset=" + down_y_offset + ']';
        }*/
    }
    public static class Cell{
        private boolean isBeen = false;
        private int XAbscissa;
        private int YOrdinate;
        private int type;
        private EnumLastAction lastAction;

        enum EnumLastAction{
            FORWARD, BACKWARD, LEFT, RIGHT
        }
    }
    public static class CellType{
        private static final int TYPE_0 = 0;
        private static final int TYPE_1 = 1;
        private static final int TYPE_2 = 2;
        private static final int TYPE_3 = 3;
        private static final int TYPE_4 = 4;
        private static final int TYPE_5 = 5;
        private static final int TYPE_6 = 6;
        private static final int TYPE_7 = 7;
        private static final int TYPE_8 = 8;
        private static final int TYPE_9 = 9;
        private static final int TYPE_10 = 10;
        private static final int TYPE_11 = 11;
        private static final int TYPE_12 = 12;
        private static final int TYPE_13 = 13;
        private static final int TYPE_14 = 14;
        private static final int TYPE_15 = 15;
    }
    public enum HttpStatus{
        OK, VALID_ERROR
    }
    public enum SideWorld {
        NORTH, SOUTH, EAST, WEST
    }
}