import com.jlu.mcloud.utils.Constant;
import com.jlu.mcloud.utils.FileUtil;
import org.junit.Test;

import java.io.IOException;
import java.util.Random;

/**
 * Created by koko on 17-3-30.
 */
public class FileTest {

    @Test
    public void splitFile() throws IOException, InterruptedException {
        System.out.println(FileUtil.currentWorkDir);
        StringBuilder sb = new StringBuilder();
        long originFileSize = Constant.MB; // 100M
        int blockSize = 15 * Constant.MB;
        String str = "aAbBcCdDeEfFgGhHiIjJkKlLmMnNoOpPqQrRsStTuUvVwWxXyYzZ0123456789";
        Random random = new Random();

        // 产生大文件
        for (int i = 0; i < originFileSize; i++) {
            sb.append(str.charAt(random.nextInt(str.length())));
        }

        String fileName = FileUtil.currentWorkDir + "/origin.txt";
        System.out.println(fileName);
        System.out.println(FileUtil.write(fileName, sb.toString()));

        //FileUtil.splitBySize(fileName, blockSize);

        //FileUtil.mergePartFiles(FileUtil.currentWorkDir, ".part",
        //        blockSize, FileUtil.currentWorkDir + "/new.file");

    }
}
