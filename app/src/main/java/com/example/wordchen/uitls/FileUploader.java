package com.example.wordchen.uitls;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.UUID;



public class FileUploader {
    private static final String TAG = "uploadFile";
    private static final int TIME_OUT = 10*10000000; //超时时间
    private static final String CHARSET = "utf-8"; //设置编码
    private static final String PREFIX = "--";
    private static final String LINE_END = "\r\n";

    public static void upload(String host, File file, Map<String,Object> params, FileUploadListener listener){
        String BOUNDARY = UUID.randomUUID().toString(); //边界标识 随机生成 String PREFIX = "--" , LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data"; //内容类型
        try {
            URL url = new URL(host);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setRequestMethod("POST"); //请求方式
            conn.setRequestProperty("Charset", CHARSET);//设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
            conn.setDoInput(true); //允许输入流
            conn.setDoOutput(true); //允许输出流
            conn.setUseCaches(false); //不允许使用缓存
            if(file!=null) {
                /** * 当文件不为空，把文件包装并且上传 */
                OutputStream outputSteam=conn.getOutputStream();
                DataOutputStream dos = new DataOutputStream(outputSteam);
                StringBuffer sb = new StringBuffer();
                sb.append(LINE_END);
                if(params!=null){//根据格式，开始拼接文本参数
                    for(Map.Entry<String,Object> entry:params.entrySet()){
                        sb.append(PREFIX).append(BOUNDARY).append(LINE_END);//分界符
                        sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINE_END);
                        sb.append("Content-Type: text/plain; charset=" + CHARSET + LINE_END);
                        sb.append("Content-Transfer-Encoding: 8bit" + LINE_END);
                        sb.append(LINE_END);
                        sb.append(entry.getValue());
                        sb.append(LINE_END);//换行！
                    }
                }
                sb.append(PREFIX);//开始拼接文件参数
                sb.append(BOUNDARY); sb.append(LINE_END);
                /**
                 * 这里重点注意：
                 * name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
                 * filename是文件的名字，包含后缀名的 比如:abc.png
                 */
                sb.append("Content-Disposition: form-data; name=\"picture\"; filename=\""+file.getName()+"\""+LINE_END);
                sb.append("Content-Type: application/octet-stream; charset="+CHARSET+LINE_END);
                sb.append(LINE_END);
                //写入文件数据
                dos.write(sb.toString().getBytes());
                InputStream is = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                long totalbytes = file.length();
                long curbytes = 0;
                Log.i("cky","total="+totalbytes);
                int len = 0;
                while((len=is.read(bytes))!=-1){
                    curbytes += len;
                    dos.write(bytes, 0, len);
                    listener.onProgress(curbytes,1.0d*curbytes/totalbytes);
                }
                is.close();
                dos.write(LINE_END.getBytes());//一定还有换行
                byte[] end_data = (PREFIX+BOUNDARY+PREFIX+LINE_END).getBytes();
                dos.write(end_data);
                dos.flush();
                /**
                 * 获取响应码 200=成功
                 * 当响应成功，获取响应的流
                 */
                int code = conn.getResponseCode();
                sb.setLength(0);
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while((line=br.readLine())!=null){
                    sb.append(line);
                }
                listener.onFinish(sb.toString(),conn.getHeaderFields());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface FileUploadListener{
         void onProgress(long pro,double precent);
         void onFinish(String res,Map<String,List<String>> headers);
    }
}