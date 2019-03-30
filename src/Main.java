import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Stack;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 宋时平 on 2019-03-27.
 */
public class Main {
    //创建一个保存网址的集合
    public static Stack mainPage = new Stack<String>();
    public static ArrayList jokesContent = new ArrayList<String>();
    public static CountDownLatch countDownLatch;
    static int threads =0;
    private static BufferedReader bf;
    public static void main(String[] args) {
        try {
            URL url = new URL("https://www.pengfue.com");
            System.out.println("正在连接...");
            URLConnection urlConnection = url.openConnection();
            bf = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder html = new StringBuilder("");
            String read = null;
            while ((read = bf.readLine())!=null){
                    html.append(read+"\n");

            }
            String reg = "<h1 class=\"dp-b\"><a href=\"(?s:(.*?))\"";
            Pattern p = Pattern.compile(reg);
            Matcher matcher = p.matcher(html);
            //提取网址

            while (matcher.find()){//此方法返回匹配到规则的下一个匹配
                String result = matcher.group();//此方法返回匹配到的结果
                result =  result.substring(26,result.length()-1);
              //  result = result.replaceFirst("<h1 class=\"dp-b\"><a href=","");

                //将处理过后的网址添加进集合
                mainPage.push(result);
                threads++;
            }
            countDownLatch = new CountDownLatch(threads);//线程等待，需要设置子线程的数量，每执行完一个子线程，线程数-1，线程数为0时，主线程执行
            for(int i=0;i<threads;i++){
                Thread t = new Thread(new JokePage());
                t.start();
            }
            countDownLatch.await();//等待所有子线程执行完毕后主线程再执行（检查线程数是否为0）
            boolean result = new WriteFile().write();
            if(result){
                System.out.println("文件写入成功");
            }else System.err.println("文件写入失败");
        } catch (UnknownHostException e){
            System.err.println("打开网址："+e.getMessage()+"失败！\n请检查网址是否输入正确或网络连接是否正常");
        } catch (InterruptedException e) {
        }catch (ConnectException e){
            System.err.println(e.getMessage()+"连接超时！");
        }
        catch (IOException e) {
            e.printStackTrace();
            e.printStackTrace();
        }finally {
            try {
                if(bf!=null)
                bf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }
}
