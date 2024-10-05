import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;

public class Robot2 {
    static int sideWorldReversSide = 0;

    static ConnectRobot connectRobot = new ConnectRobot();
    static SensorData sensorData;

    public static void main(String[] args) throws java.lang.Exception{
        while (true) {
            sensorData = ConnectSensorData.getSensorData();
            int actualType = isActualCellType(sensorData);

            if (actualType == 12) {
                sideWorldReversSide = (sideWorldReversSide + 2) % 4;
                connectRobot.right();
                connectRobot.right();
                connectRobot.forward();
            } else if (actualType == 0 || (actualType >= 2 && actualType <= 4) || actualType == 7) {
                sideWorldReversSide = (sideWorldReversSide - 1 + 4) % 4;
                connectRobot.left();
                connectRobot.forward();
            } else if (actualType == 14 || actualType == 1 || actualType == 9 || actualType == 5) {
                connectRobot.forward();
            } else if (actualType == 8 || actualType == 13) {
                sideWorldReversSide = (sideWorldReversSide + 1) % 4;
                connectRobot.right();
                connectRobot.forward();
            }
        }
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
}
