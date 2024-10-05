/*
package result;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashSet;
import java.util.Set;

public class RobotResult1 {
    static int sideWorldReversSide = 0;
    static int xAbscissa = 15;
    static int yOrdinate = 0;

    static Set<Cell> typeCell = new HashSet<>();

    static ConnectRobot connectRobot = new ConnectRobot();
    static ConnectSensorData connectSensorData = new ConnectSensorData();

    static int[][] matrix = new int[16][16];

    public static void main(String[] args) throws java.lang.Exception{

        SensorData sensorData;
        while (typeCell.size() <= 255) {
            sensorData = connectSensorData.getSensorData();
            if (isRevers(sensorData)) {
                createCell(isCellType(sensorData));

                if (sideWorldReversSide == 3) sideWorldReversSide = 1;
                else if (sideWorldReversSide == 2) sideWorldReversSide = 0;
                else if (sideWorldReversSide == 1) sideWorldReversSide = 3;
                else if (sideWorldReversSide == 0) sideWorldReversSide = 2;

                connectRobot.right();
                connectRobot.right();
                connectRobot.forward();

                setPosition();
            } else if (isLeft(sensorData)) {
                createCell(isCellType(sensorData));

                if (sideWorldReversSide <= 3 && sideWorldReversSide >= 1) sideWorldReversSide--;
                else if (sideWorldReversSide == 0) sideWorldReversSide = 3;
                connectRobot.left();
                connectRobot.forward();

                setPosition();
            } else if (isForward(sensorData)) {
                createCell(isCellType(sensorData));

                connectRobot.forward();

                setPosition();
            } else if (isRight(sensorData)) {
                createCell(isCellType(sensorData));

                if (sideWorldReversSide >= 0 && sideWorldReversSide <= 2) sideWorldReversSide++;
                else if (sideWorldReversSide == 3) sideWorldReversSide = 0;
                connectRobot.right();
                connectRobot.forward();

                setPosition();
            }
        }
        for (Cell cell : typeCell) {
            matrix[cell.XAbscissa][cell.YOrdinate] = cell.type;
        }
        sendMatrix(matrix);
    }

    public static void createCell(int cellType) {
        short existingCell = (short) typeCell.stream()
                .filter(cell -> cell.XAbscissa == xAbscissa && cell.YOrdinate == yOrdinate)
                .count();
        if (existingCell == 0) {
            Cell cell = new Cell();
            cell.isBeen = true;
            cell.type = getCellType(cellType);
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

    private static void sendMatrix(int[][] matrix) {
        connectRobot.sendMatrix(matrix);
    }
    private static boolean isRevers(SensorData sensorData) {
        return (sensorData.front_distance < SensorData.DISTANCE_NEAREST_CELL) && (sensorData.right_side_distance < SensorData.DISTANCE_NEAREST_CELL) && (sensorData.left_side_distance < SensorData.DISTANCE_NEAREST_CELL);
    }
    private static boolean isRight(SensorData sensorData) {
        return (sensorData.front_distance < SensorData.DISTANCE_OPEN_CELL && sensorData.left_side_distance < SensorData.DISTANCE_OPEN_CELL);
    }
    private static boolean isLeft(SensorData sensorData) {
        if ((sensorData.front_distance > SensorData.DISTANCE_NEAREST_CELL && sensorData.front_distance < SensorData.DISTANCE_OPEN_CELL)
                && (sensorData.left_side_distance > SensorData.DISTANCE_NEAREST_CELL && sensorData.left_side_distance < SensorData.DISTANCE_OPEN_CELL)
                && (sensorData.right_side_distance > SensorData.DISTANCE_NEAREST_CELL && sensorData.right_side_distance < SensorData.DISTANCE_OPEN_CELL)) {
            return true;
        } else if ((sensorData.front_distance > SensorData.DISTANCE_NEAREST_CELL && sensorData.front_distance < SensorData.DISTANCE_OPEN_CELL)
                && (sensorData.left_side_distance > SensorData.DISTANCE_NEAREST_CELL && sensorData.left_side_distance < SensorData.DISTANCE_OPEN_CELL)) {
            return true;
        } else if ((sensorData.right_side_distance > SensorData.DISTANCE_NEAREST_CELL && sensorData.right_side_distance < SensorData.DISTANCE_OPEN_CELL)
                && (sensorData.left_side_distance > SensorData.DISTANCE_NEAREST_CELL && sensorData.left_side_distance < SensorData.DISTANCE_OPEN_CELL)) {
            return true;
        } else
            return sensorData.front_distance < SensorData.DISTANCE_NEAREST_CELL && sensorData.right_side_distance < SensorData.DISTANCE_NEAREST_CELL;
    }
    private static boolean isForward(SensorData sensorData) {
        if (sensorData.right_side_distance < SensorData.DISTANCE_NEAREST_CELL && sensorData.left_side_distance < SensorData.DISTANCE_NEAREST_CELL) {
            return true;
        } else
            return (sensorData.front_distance > SensorData.DISTANCE_NEAREST_CELL && sensorData.front_distance < SensorData.DISTANCE_OPEN_CELL)
                    && (sensorData.right_side_distance > SensorData.DISTANCE_NEAREST_CELL && sensorData.right_side_distance < SensorData.DISTANCE_OPEN_CELL);
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

        public HttpClient getHttpClient() {
            return HttpClient.newHttpClient();
        }

        public void right() {
            HttpClient client = getHttpClient();
            HttpRequest request = createRequestMovePOST_notBody(RIGHT);
            try {
                sendRequest_notResponseBody(client, request);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        public void left() {
            HttpClient client = getHttpClient();
            HttpRequest request = createRequestMovePOST_notBody(LEFT);
            try {
                sendRequest_notResponseBody(client, request);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        public void forward() {
            HttpClient client = getHttpClient();
            HttpRequest request = createRequestMovePOST_notBody(FORWARD);
            try {
                sendRequest_notResponseBody(client, request);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        public void sendMatrix(int[][] matrix) {
            HttpClient client = getHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL_ROBOT + SEND_MATRIX + "?token=" + TOKEN))
                    .header("Content-Type", "application/json;")
                    .POST(HttpRequest.BodyPublishers.ofString(toJson(matrix)))
                    .build();
            try {
                System.out.println(client.send(request, HttpResponse.BodyHandlers.ofString()).body());
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        public HttpRequest createRequestMovePOST_notBody(String requestEndpoint) {
            return HttpRequest.newBuilder()
                    .uri(URI.create(URL_ROBOT + requestEndpoint + "?token=" + TOKEN))
                    .POST(HttpRequest.BodyPublishers.noBody())
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

        public void sendRequest_notResponseBody(HttpClient client, HttpRequest request) throws IOException, InterruptedException {
            client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        public HttpResponse<String> sendRequest(HttpClient client, HttpRequest request) throws IOException, InterruptedException {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        }
    }
    public static class ConnectSensorData {
        private static final String SENSOR_DATA_ENDPOINT = "/robot-cells/sensor-data";
        private static final ConnectRobot connectRobot = new ConnectRobot();

        public SensorData getSensorData() {
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
            JsonNode jsonNode;
            try {
                jsonNode = objectMapper.readTree(response.body());
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            return jsonNode.get(param).floatValue();
        }
    }
    public record SensorData(Float front_distance, Float right_side_distance, Float left_side_distance,
                             Float back_distance, Float left_45_distance, Float right_45_distance, Float rotation_pitch,
                             Float rotation_yaw, Float rotation_roll, Float down_x_offset, Float down_y_offset) {
        public static final Float DISTANCE_NEAREST_CELL = 65F;
        public static final Float DISTANCE_OPEN_CELL = 2000F;
    }
    public static class Cell {
        public boolean isBeen = false;
        public int XAbscissa;
        public int YOrdinate;
        public int type;
    }
}
*/
