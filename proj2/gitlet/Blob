package gitlet;

import java.io.Serializable;

public class Blob implements Serializable {
    private String content;
    private String blobId;
public Blob(String s){
    content=s;
}
public void calculate(){
    blobId=Utils.sha1(content);
}
    public String getBlobId() {
    calculate();
        return blobId;
    }
}
