package app.expert.parsers.file_parser;

import app.expert.db.uploaded_files.UploadedFile;
import app.expert.db.uploaded_files.UploadedFileRepository;
import lombok.*;
import org.springframework.stereotype.Service;
import java.io.*;
import java.util.Date;

@Service
@Data
public class FileSaver implements Runnable{

    private UploadedFileRepository upFileRepo;
    private String content;

    /**
     * Сохраняем полученный контент на диск, присваиваем соответствующее имя
     */
    private void saveFile(String content) throws IOException {
        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setContent(content);
        uploadedFile.setSize(getContentSize(content));
        uploadedFile.setUploaded(new Date());
        InputStream is = new ByteArrayInputStream(content.getBytes());
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        br.readLine();
        String secondLine = br.readLine();
        if (secondLine.startsWith("3")) {
            uploadedFile.setName(FilePathHolder.getAbc3());
        }
        else if (secondLine.startsWith("4")) {
            uploadedFile.setName(FilePathHolder.getAbc4());
        }
        else if (secondLine.startsWith("8")) {
            uploadedFile.setName(FilePathHolder.getAbc8());
        }
        else if (secondLine.startsWith("9")) {
            uploadedFile.setName(FilePathHolder.getDef9());
        }
        br.close();
        is.close();
        upFileRepo.save(uploadedFile);
    }

    private String getContentSize(String content) {
        byte[] bytes = content.getBytes();
        return getMB(bytes.length);
    }

    private String getMB(long  bit){
        return String.valueOf(((double)bit)/(1024*1024)).substring(0, 4) + "MB";
    }

    @SneakyThrows
    @Override
    public void run() {
        saveFile(this.content);
    }
}
