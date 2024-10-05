/*
package robot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class RobotTest {
    static int sideWorldReversSide = 0;
    static int xAbscissa = 15;
    static int yOrdinate = 0;

    static Set<Cell> typeCell = new HashSet<>();

    static ConnectRobot connectRobot = new ConnectRobot();

    static int[][] matrix = new int[16][16];

    public static void main(String[] args) throws java.lang.Exception{

        SensorData sensorData;
        while (typeCell.size() <= 255) {
            sensorData = ConnectSensorData.getSensorData();
            if (isRevers(sensorData)) {
                createCell(isActualCellType(sensorData));

                if (sideWorldReversSide == 3) sideWorldReversSide = 1;
                else if (sideWorldReversSide == 2) sideWorldReversSide = 0;
                else if (sideWorldReversSide == 1) sideWorldReversSide = 3;
                else if (sideWorldReversSide == 0) sideWorldReversSide = 2;

                connectRobot.right();
                connectRobot.right();
                connectRobot.forward();

                setPosition();
            } else if (isLeft(sensorData)) {
                createCell(isActualCellType(sensorData));

                if (sideWorldReversSide <= 3 && sideWorldReversSide >= 1) sideWorldReversSide--;
                else if (sideWorldReversSide == 0) sideWorldReversSide = 3;
                connectRobot.left();
                connectRobot.forward();

                setPosition();
            } else if (isForward(sensorData)) {
                createCell(isActualCellType(sensorData));

                connectRobot.forward();

                setPosition();
            } else if (isRight(sensorData)) {
                createCell(isActualCellType(sensorData));

                if (sideWorldReversSide >= 0 && sideWorldReversSide <= 2) sideWorldReversSide++;
                else if (sideWorldReversSide == 3) sideWorldReversSide = 0;
                connectRobot.right();
                connectRobot.forward();

                setPosition();
            }
        }
        for (Cell cell : typeCell) {
            matrix[cell.XAbscissa][cell.YOrdinate] = getCellType(cell.type, cell.sideWorld);
        }
        sendMatrix(matrix);
    }

    public static void createCell(int cellType) {
        short existingCell = (short) typeCell.stream()
                .filter(cell -> cell.XAbscissa == xAbscissa && cell.YOrdinate == yOrdinate)
                .count();
        if (existingCell == 0) {
            Cell cell = new Cell();
            cell.type = cellType;
            cell.sideWorld = sideWorldReversSide;
            cell.YOrdinate = yOrdinate;
            cell.XAbscissa = xAbscissa;
            typeCell.add(cell);
        }
    }
    public static void setPosition() {
        if (sideWorldReversSide == 0) xAbscissa--;
        else if (sideWorldReversSide == 1) yOrdinate++;
        else if (sideWorldReversSide == 3) yOrdinate--;
        else xAbscissa++;
    }

    public static int getCellType(int actualType, int sideWorldReversSide) {
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
            }else if (actualType == 0) return 0;
            else return actualType;
        } else if (sideWorldReversSide == 2) {
            if (actualType >= 11 && actualType <= 14) {
                return actualType + sideWorldReversSide;
            } else if (actualType >= 5 && actualType <= 8) {
                return actualType - sideWorldReversSide;
            } else if (actualType >= 1 && actualType <= 4) {
                if (actualType == 3) return actualType - sideWorldReversSide;
                return actualType + sideWorldReversSide;
            } else return actualType;
        } else return actualType;
    }

    public static int isActualCellType(SensorData sensorData) {
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

    private static void sendMatrix(int[][] matrix) throws IOException {
        connectRobot.sendMatrix(matrix);
    }
    private static boolean isRevers(SensorData sensorData) {
        return isActualCellType(sensorData) == 12;
    }
    private static boolean isRight(SensorData sensorData) {
        return isActualCellType(sensorData) == 8;
    }
    private static boolean isLeft(SensorData sensorData) {
        int actualType = isActualCellType(sensorData);
        return actualType == 0 || (actualType >= 2 && actualType <= 4) || actualType == 7;
    }
    private static boolean isForward(SensorData sensorData) {
        int actualType = isActualCellType(sensorData);
        return actualType == 14 || actualType == 1 || actualType == 9 || actualType == 5;
    }

    private static boolean isWall1(SensorData sensorData) {
        return (sensorData.left_side_distance < SensorData.DISTANCE_NEAREST_CELL);
    }
    private static boolean isWall2(SensorData sensorData) {
        return (sensorData.front_distance < SensorData.DISTANCE_NEAREST_CELL);
    }
    private static boolean isWall3(SensorData sensorData) {
        return sensorData.right_side_distance < SensorData.DISTANCE_NEAREST_CELL;
    }
    private static boolean isWall4(SensorData sensorData) {
        return sensorData.back_distance < SensorData.DISTANCE_NEAREST_CELL;
    }
    private static boolean isWall5(SensorData sensorData) {
        return sensorData.left_side_distance < SensorData.DISTANCE_NEAREST_CELL && sensorData.back_distance < SensorData.DISTANCE_NEAREST_CELL;
    }
    private static boolean isWall6(SensorData sensorData) {
        return (sensorData.right_side_distance < SensorData.DISTANCE_NEAREST_CELL && sensorData.back_distance < SensorData.DISTANCE_NEAREST_CELL);
    }
    private static boolean isWall7(SensorData sensorData) {
        return sensorData.right_side_distance < SensorData.DISTANCE_NEAREST_CELL && sensorData.front_distance < SensorData.DISTANCE_NEAREST_CELL;
    }
    private static boolean isWall8(SensorData sensorData) {
        return sensorData.left_side_distance < SensorData.DISTANCE_NEAREST_CELL && sensorData.front_distance < SensorData.DISTANCE_NEAREST_CELL;
    }
    private static boolean isWall9(SensorData sensorData) {
        return sensorData.right_side_distance < SensorData.DISTANCE_NEAREST_CELL && sensorData.left_side_distance < SensorData.DISTANCE_NEAREST_CELL;
    }
    private static boolean isWall10(SensorData sensorData) {
        return sensorData.front_distance < SensorData.DISTANCE_NEAREST_CELL && sensorData.back_distance < SensorData.DISTANCE_NEAREST_CELL;
    }
    private static boolean isWall11(SensorData sensorData) {
        return sensorData.right_side_distance < SensorData.DISTANCE_NEAREST_CELL
                && sensorData.front_distance < SensorData.DISTANCE_NEAREST_CELL
                && sensorData.back_distance < SensorData.DISTANCE_NEAREST_CELL;
    }
    private static boolean isWall12(SensorData sensorData) {
        return sensorData.right_side_distance < SensorData.DISTANCE_NEAREST_CELL
                && sensorData.left_side_distance < SensorData.DISTANCE_NEAREST_CELL
                && sensorData.front_distance < SensorData.DISTANCE_NEAREST_CELL;
    }
    private static boolean isWall13(SensorData sensorData) {
        return sensorData.left_side_distance < SensorData.DISTANCE_NEAREST_CELL
                && sensorData.front_distance < SensorData.DISTANCE_NEAREST_CELL
                && sensorData.back_distance < SensorData.DISTANCE_NEAREST_CELL;
    }
    private static boolean isWall14(SensorData sensorData) {
        return sensorData.right_side_distance < SensorData.DISTANCE_NEAREST_CELL
                && sensorData.left_side_distance < SensorData.DISTANCE_NEAREST_CELL
                && sensorData.back_distance < SensorData.DISTANCE_NEAREST_CELL;
    }
    private static boolean isWall15(SensorData sensorData) {
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
        private final static String SEND_MATRIX = "/matrix/send";

        private static final OkHttpClient client = new OkHttpClient();

        public OkHttpClient getOkHttpClient(){
            return client;
        }

        private void executeRequest(Request request) {
            try {
                client.newCall(request).execute();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public void right() {
            Request request = createRequestMovePOST_notBody(RIGHT);
            try (Response response = client.newCall(request).execute()) {
                // ничего не делать с ответом, если он не нужен
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public void left() {
            Request request = createRequestMovePOST_notBody(LEFT);
            try (Response response = client.newCall(request).execute()) {
                // ничего не делать с ответом, если он не нужен
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public void forward() {
            Request request = createRequestMovePOST_notBody(FORWARD);
            try (Response response = client.newCall(request).execute()) {
                // ничего не делать с ответом, если он не нужен
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public void sendMatrix(int[][] matrix) throws IOException {
            Request request = createRequestMovePOST(SEND_MATRIX);
            try (Response response = client.newCall(request).execute()) {
                System.out.println(response.body().string());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public Request createRequestMovePOST(String requestEndpoint) {
            return new Request.Builder()
                    .url(URL_ROBOT + requestEndpoint + "?token=" + TOKEN)
                    .post(RequestBody.create(toJson(matrix), MediaType.parse("application/json; charset=utf-8")))
                    .build();
        }

        public Request createRequestMovePOST_notBody(String requestEndpoint) {
            return new Request.Builder()
                    .url(URL_ROBOT + requestEndpoint + "?token=" + TOKEN)
                    .post(RequestBody.create(new byte[0]))
                    .build();
        }

        public String toJson(int[][] matrix) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                return mapper.writeValueAsString(matrix);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        public static void close(){
            client.dispatcher().executorService().shutdown();
        }
    }
    public static class ConnectSensorData {
        private static final String SENSOR_DATA_ENDPOINT = "/robot-cells/sensor-data";
        private static final ConnectRobot connectRobot = new ConnectRobot();

        public static Request createRequestMoveGET(String requestEndpoint) {
            return new Request.Builder()
                    .url(ConnectRobot.URL_ROBOT + requestEndpoint + "?token=" + ConnectRobot.TOKEN)
                    .get()
                    .build();
        }

        public static SensorData getSensorData() {
            OkHttpClient client = connectRobot.getOkHttpClient();
            Request request = createRequestMoveGET(SENSOR_DATA_ENDPOINT);
            Call call = client.newCall(request);
            try (Response response = call.execute()) {
                assert response.body() != null;
                String responseBody = response.body().string();
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode;
                try {
                    jsonNode = objectMapper.readTree(responseBody);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

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
        public int type;
        public int sideWorld;
    }
}
*/
