import com.jlu.mcloud.communicate.fileio.FileIoHandler;
import com.jlu.mcloud.communicate.fileio.IFileTransHandler;
import com.jlu.mcloud.communicate.heartbeat.HeartbeatHandler;
import com.jlu.mcloud.communicate.login.LoginHandler;
import com.jlu.mcloud.communicate.regist.RegistHandler;
import com.jlu.mcloud.communicate.respond.RespondHandler;
import com.jlu.mcloud.config.Config;
import com.jlu.mcloud.rpc.server.ServiceCenter;
import com.jlu.mcloud.service.*;

import java.io.IOException;

/**
 * Created by koko on 2017/3/16.
 */
public class ServerMain {

    public static ServiceCenter serviceCenter = ServiceCenter.getInstance(Config.SERVER_PORT);
    public static void main(String[] args) throws IOException {
        register();
        serviceCenter.start();
    }

    public static void register() {
        serviceCenter.register(HelloService.class, HelloServiceImpl.class);
        serviceCenter.register(HeartbeatHandler.class, HeartbeatHandlerImpl.class);
        serviceCenter.register(FileIoHandler.class, FileIoHandlerImpl.class);
        serviceCenter.register(RespondHandler.class, RespondHandlerImpl.class);
        serviceCenter.register(LoginHandler.class, LoginHandlerImpl.class);
        serviceCenter.register(RegistHandler.class, RegistHandlerImpl.class);
        serviceCenter.register(IFileTransHandler.class, FileTransHandlerImpl.class);
    }
}
