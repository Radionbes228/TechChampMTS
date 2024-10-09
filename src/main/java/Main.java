import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Main {

    // TODO: отличная идея управление моторами через цикл, вот только нужна задержка на основе времени работы мотора

    static ConnectRobot connectRobot = new ConnectRobot();
    static SensorData sensorData;

    public static void main(String[] args) throws IOException, InterruptedException {
        whileMoveRobotForward();
    }


    public static void whileMoveRobotForward() throws IOException, InterruptedException {
        while (true) {
            sensorData = ConnectSensorData.getSensorData();
            if (sensorData.left_45_distance > 90F){
                whileMoveRobotLeft();
            }else if (sensorData.right_45_distance > 90F){
                whileMoveRobotRight();
            }
            if (sensorData.front_distance > 60F) {
                connectRobot.moveMotors(200, 0.2f, 200, 0.2f);
            }else return;
        }
    }

    public static void whileMoveRobotRight() throws IOException, InterruptedException {
        while (true) {
            sensorData = ConnectSensorData.getSensorData();
            if (sensorData.left_45_distance < 90F && sensorData.right_45_distance < 90F) return;
            else connectRobot.moveMotors(170, 0.2f, -50, 0.2f);
        }
    }

    public static void whileMoveRobotLeft() throws IOException, InterruptedException {
        while (true) {
            sensorData = ConnectSensorData.getSensorData();
            if (sensorData.left_45_distance < 100F && sensorData.right_45_distance < 100F) return;
            else {
                connectRobot.moveMotors(-50, 0.2f, 170, 0.2f);
            }
            connectRobot.moveMotors(170, 0.2f, -50, 0.2f);
        }
    }

    public static class ConnectRobot {
        private final static String TOKEN = "a6351cec-6f32-4e4d-a802-69e46add4b55f3066094-5f04-4619-8870-05455bd3fe1e";
        private final static String URL_ROBOT = "http://127.0.0.1:8801/api/v1";
        private final static String RESTART = "/maze/restart";
        private final static String MOTORS_MOVE = "/robot-motors/move";

        private static final OkHttpClient client;
        static {
            client = new OkHttpClient.Builder()
                    .connectionPool(new ConnectionPool(20, 10, TimeUnit.MINUTES))
                    .build();
        }

        public void restart() throws IOException, InterruptedException {
            requestSend(RESTART);
        }

        public void moveMotors(int l_speed, float l_time, int r_speed, float r_time) throws IOException, InterruptedException {
            requestSendMoveMotors(l_speed, l_time, r_speed, r_time);
        }

        private void requestSendMoveMotors(int lSpeed, float lTime, int rSpeed, float rTime) throws IOException {
            String url = URL_ROBOT +
                    MOTORS_MOVE +
                    "?l=" +
                    lSpeed +
                    "&l_time=" +
                    lTime +
                    "&r=" +
                    rSpeed +
                    "&r_time=" +
                    rTime;
            Request request = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(new byte[0]))
                    .build();
            Response response = client.newCall(request).execute();
            response.close();
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