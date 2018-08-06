package tdiant.rgr;

/**
 * Created by tdiant on 2018/8/7 0007.
 */
public class RefreshThread extends Thread {
    @Override
    public void run(){
        System.out.println("System checking start.");
        if(RuaGithubRefresher.checker()){
            RuaGithubRefresher.update();
        }
        try {
            Thread.sleep(RuaGithubRefresher.coolTime);
        } catch (InterruptedException e) {}
        //this.run();
        new RefreshThread().start();
    }
}
