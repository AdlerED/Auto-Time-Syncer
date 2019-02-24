import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 * @author Adlered QQ1101635162
 */
public class Adlered2 {

    private static int sleepMinutes = 0;
    private static final long EPOCH_OFFSET_MILLIS;
    private static final String[] hostName = { "www.time.ac.cn", "time.nist.gov" };
    // private static final String[] hostName = { "192.168.16.127" };

    static {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        // Java使用的参照标准是1970年，而时间服务器返回的秒是相当1900年的，算一下偏移
        calendar.set(1900, Calendar.JANUARY, 1, 0, 0, 0);
        EPOCH_OFFSET_MILLIS = Math.abs(calendar.getTime().getTime());
    }

    public static void main(String[] args) {
        // 检测电脑是否在线
        // while (offLine() && sleepMinutes < 30) {
        // try {
        // Thread.sleep(120000);
        // sleepMinutes += 2;
        // } catch (InterruptedException ex) {
        // Logger.getLogger(Adlered2.class.getName()).log(Level.SEVERE, null,
        // ex);
        // }
        // }
        //        
        // 30分钟还没有联线，表示就不上网了，退出吧
        // if (sleepMinutes >= 30) {
        // System.exit(0);
        // }

        // 从网络上获取时间
        Date date = null;
        for (int i = 0; i < hostName.length; i++) {
            date = getNetDate(hostName[i]);
            if (date != null) {
                System.out.println(hostName[i] + "获取到时间成功..." );
                break;
            } else {
                System.out.println(hostName[i] + "未获取到时间！！！");
            }

        }

        // 修改本机时间
        if (date != null) {
            setComputeDate(date);
        }
    }

    public static void Deletetmp() {
        try //延时
        {
            Thread.sleep(2000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        File f2 = new File("tmp.bat"); //写入bat
        f2.delete();
    }

    private static Date getNetDate(String hostName) {
        System.out.println("开始从网络获取时间，使用服务器" + hostName);
        Date date = null;
        long result = 0;
        try {
            Socket socket = new Socket(hostName, 37);
            BufferedInputStream bis = new BufferedInputStream(socket.getInputStream(), socket.getReceiveBufferSize());
            int b1 = bis.read();
            int b2 = bis.read();
            int b3 = bis.read();
            int b4 = bis.read();
            if ((b1 | b2 | b3 | b3) < 0) {
                return null;
            }
            result = (((long) b1) << 24) + (b2 << 16) + (b3 << 8) + b4;
            date = new Date(result * 1000 - EPOCH_OFFSET_MILLIS);
            socket.close();
        } catch (UnknownHostException ex) {
            Logger.getLogger(Adlered2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Adlered2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return date;
    }

    /**
     * 通过ping命令判断是否离线
     *
     * @return
     */
    private static boolean offLine() {
        Runtime run = Runtime.getRuntime();
        try {
            Process process = run.exec("ping www.hao123.com");
            InputStream s = process.getInputStream();
            BufferedReader bis = new BufferedReader(new InputStreamReader(s));
            String str = bis.readLine();
            while (str != null) {
                if (str.startsWith("Reply from")) {
                    return false;
                }
                str = bis.readLine();
            }
            process.waitFor();
        } catch (IOException ex) {
            Logger.getLogger(Adlered2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(Adlered2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    /**
     * 通过调用本地命令date和time修改计算机时间
     *
     * @param date
     */
    private static void setComputeDate(Date date) {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        c.setTime(date);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);

        c.setTime(new Date());
        int year_c = c.get(Calendar.YEAR);
        int month_c = c.get(Calendar.MONTH) + 1;
        int day_c = c.get(Calendar.DAY_OF_MONTH);
        int hour_c = c.get(Calendar.HOUR_OF_DAY);
        int minute_c = c.get(Calendar.MINUTE);

        String ymd = year + "-" + month + "-" + day;
        String time = hour + ":" + minute + ":" + second;
        boolean modifyDate = false;
        boolean modifyTime = false;
        try {
            // 修改日期
            if (year != year_c || month != month_c || day != day_c) {
                String cmd = "cmd /c date " + ymd;
                Process process = Runtime.getRuntime().exec(cmd);
                process.waitFor();
                modifyDate = true;
            }

            // 修改时间
            if (hour != hour_c || minute != minute_c) {
                String cmd = "time " + time;
				File f1 = new File("tmp.bat"); //写入bat
                //try {
                    //if (!f1.exists()) //没卵用
                    //f1.getParentFile().mkdirs();
                    f1.createNewFile();
                    byte data[] = cmd.getBytes();
                    FileOutputStream fos = new FileOutputStream(f1);
                    for (int x = 0; x < data.length; x++) {
                        fos.write(data[x]); // writes the bytes
                    }
                    fos.close();
                /*} catch (Exception e) {
                    e.printStackTrace();
                }*/
				System.out.println("写入成功");
                /*Process process = Runtime.getRuntime().exec("rundll32 url.dll FileProtocolHandler  tmp.bat"); //这种方法没法提权，不用了
                process.waitFor();*/
                String adminrun = "nircmd.exe elevate tmp.bat";
                Process process2 = Runtime.getRuntime().exec(adminrun); //这种方法没法提权，不用了
                process2.waitFor();
                /*if(f1.delete())
                    System.out.println("File deleted.");*/
                modifyTime = true;
                Deletetmp();
            }

            if (modifyDate) {
                System.out.println("日期已被修改，修改前日期：" + year_c + "-" + month_c + "-" + day_c + "\n修改后日期：" + ymd);
            }

            if (modifyTime) {
                System.out.println("时间已被修改，修改前时间：" + hour_c + ":" + minute_c + ":" + c.get(Calendar.SECOND)
                        + "\n修改后时间：" + time);
            }

            if (!modifyDate && !modifyTime) {
                System.out.println("当前时间与网络时间一致..." + "当前时间：" + ymd + " " + time);
            }
        } catch (IOException ex) {
            Logger.getLogger(Adlered2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(Adlered2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
