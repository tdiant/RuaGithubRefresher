package tdiant.rgr;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;
import tdiant.bukkit.bdn.GitbookProcess;

import java.io.*;

/**
 * Created by tdiant on 2018/8/6 0006.
 * IMPORT: https://blog.csdn.net/yeweiouyang/article/details/53665290
 */
public class RuaGithubRefresher {
    private static String lastSHA = "RUA!";

    //============设置项目==============
    public static IPreProcess process = new GitbookProcess();
    public static int coolTime = 2*60*1000; //单位 毫秒 - Default 2min
    public static String url = "https://api.github.com/repos/tdiant/BukkitDevelopmentNote/commits";
    public static String fileSaveDir = ""; //末尾不用缀斜杠
    public static String tempFileName = "rua.json";

    public static void main(String[] args) throws IOException {
        System.out.println("Rua Github Refresher - System running.");
        new RefreshThread().start();
    }

    public static void callShell(String shellString) {
        try {
            Process process = Runtime.getRuntime().exec(shellString);
            int exitValue = process.waitFor();
            if (0 != exitValue) {
                System.err.println("call shell failed. error code is :" + exitValue);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static boolean checker(){
        String info = "None.";
        try {
            DownloadTool.downLoadFromUrl(url , tempFileName ,fileSaveDir);
            info = reader(fileSaveDir + "/" + tempFileName);
            JSONArray json = JSONObject.parseArray(info);
            if(!((JSONObject)json.get(0)).getString("sha").equalsIgnoreCase(lastSHA)) {
                lastSHA = ((JSONObject)json.get(0)).getString("sha");
                return true;
            }
        } catch (Exception e) {
            System.out.println("Something wrong happened.");
            System.out.println("Error Data: \r\n" + info);
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public static void update(){
        process.beforeUpdate();
        gitPull(new File(fileSaveDir + ""));
        process.afterUpdate();
    }

    public static String cloneRepository(String url,String localPath) {
        try{
            System.out.println("开始下载......");

            CloneCommand cc = Git.cloneRepository().setURI(url);
            cc.setDirectory(new File(localPath)).call();

            System.out.println("下载完成......");

            return "success";
        }catch(Exception e)
        {
            e.printStackTrace();
            return "error";
        }
    }

    public static void gitPull(File repoDir) {
        File RepoGitDir = new File(repoDir.getAbsolutePath() + "/.git");
        if (!RepoGitDir.exists()) {
            System.out.println("Error! Not Exists : " + RepoGitDir.getAbsolutePath());
        } else {
            Repository repo = null;
            try {
                repo = new FileRepository(RepoGitDir.getAbsolutePath());
                Git git = new Git(repo);
                PullCommand pullCmd = git.pull();
                pullCmd.call();

                System.out.println("Pulled from remote repository to local repository at " + repo.getDirectory());
            } catch (Exception e) {
                System.out.println(e.getMessage() + " : " + RepoGitDir.getAbsolutePath());
            } finally {
                if (repo != null) {
                    repo.close();
                }
            }
        }
    }

    private static String reader(String fileName) {

        String Path= fileName;
        BufferedReader reader = null;
        String laststr = "";
        try {
            FileInputStream fileInputStream = new FileInputStream(Path);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
            reader = new BufferedReader(inputStreamReader);
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                laststr += tempString;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return laststr;
    }
}
