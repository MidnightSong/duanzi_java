import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JokePage implements Runnable {
    BufferedReader bf;
    @Override
    public void run() {
        String pop = (String) Main.mainPage.pop();
        try {
            URL url = new URL(pop);
            URLConnection urlConnection = url.openConnection();
            bf = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder("");
            char[] ct = new char[4096];
            while(bf.read(ct)!=-1){
                sb.append(ct);
            }
            Pattern regTitle = Pattern.compile("<h1>(?s:(.*?))</h1>");//.*?设置成非贪婪模式
            Matcher matcher = regTitle.matcher(sb);
            matcher.find();
            String title = matcher.group();
            title = title.replaceAll("\t","");
            title = title.replaceAll("<h1>","");
            title = title.replaceAll("</h1>","");

            Pattern regContent = Pattern.compile("<div class=\"content-txt pt10\">(?s:(.*?))<a id=\"prev\"");//.*?设置成非贪婪模式
            matcher = regContent.matcher(sb);
            matcher.find();
            String content = matcher.group();

            content = content.replaceAll("<br />","");
            content = content.replaceAll("\t","");
            content = content.replaceAll("&nbsp;","");
            content = content.replaceAll("<div class=\"content-txt pt10\">","");
            content = content.replaceAll("<a id=\"prev\"","");


            StringBuilder jokeContent = new StringBuilder(title+"\n"+content+"\n-------------------------------\n");
            Main.jokesContent.add(jokeContent);
        }catch (MalformedURLException e) {
            e.printStackTrace();
        }catch (ConnectException e){
            System.err.println(e.getMessage()+"连接超时！");
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (bf!=null){
                bf.close();}
                Main.countDownLatch.countDown();//线程执行完后执行，使线程数减少1
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
