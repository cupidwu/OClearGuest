package com.cupid.wifi;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cupid.wifi.db.KeyAccess;
import com.cupid.wifi.model.WebLoginInfo;
import com.cupid.wifi.server.ServerInfo;
import com.cupid.wifi.util.AsynTaskResult;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MainActivity";

    private ProgressDialog pDlg, keyDlg;
    private Button loginBtn;
    private EditText pwdText;

    private String serverPwd = "";

    private Handler pHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            pDlg.dismiss();
            switch (AsynTaskResult.valueOf(msg.what)) {
                case SUCCESS:
                    Toast.makeText(MainActivity.this, "Login success...", Toast.LENGTH_LONG).show();
                    break;
                case FAILED:
                    Toast.makeText(MainActivity.this, "Login failed...", Toast.LENGTH_LONG).show();
                    break;
                default:
                    Toast.makeText(MainActivity.this, "Wired exception...", Toast.LENGTH_LONG).show();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private Handler keyHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            keyDlg.dismiss();
            Bundle b = msg.getData();
            pwdText.setText(b.getString("pwd"));
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        this.stopService(new Intent(this, WifiService.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginBtn = (Button) findViewById(R.id.loginBtn);
        pwdText = (EditText) findViewById(R.id.pwdText);

        pwdText.setText(KeyAccess.getKey(MainActivity.this));

        //update server url info
        ServerInfo.setCurServerURL(KeyAccess.getServerURL(MainActivity.this));

        if (pwdText.getText().toString().length() == 0) {
            //get the password from network
            keyDlg = new ProgressDialog(MainActivity.this);
            keyDlg.setTitle("Fetch Key");
            keyDlg.setMessage("Getting key from server...");
            keyDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            keyDlg.setCancelable(true);
            keyDlg.show();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Message msg = keyHandler.obtainMessage();
                    Bundle b = new Bundle();
                    serverPwd = ServerInfo.getServerPwd();

                    b.putString("pwd", serverPwd);
                    msg.setData(b);

                    msg.sendToTarget();
                }
            }).start();

        }

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDlg = new ProgressDialog(MainActivity.this);
                pDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pDlg.setMessage("Loading....");
                pDlg.setTitle("Login to Oracle Clear Guest");
                pDlg.show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (WebLoginInfo.loginProcess(pwdText.getText().toString())) {
                            pHandler.sendEmptyMessage(AsynTaskResult.SUCCESS.value());
                            if (!serverPwd.equals(pwdText.getText().toString())) {

                                //upload the key to the server...
                                ServerInfo.setCurServerURL(KeyAccess.getServerURL(MainActivity.this));
                                ServerInfo.uploadPwd(pwdText.getText().toString());

                            } else {
                                KeyAccess.storeNewKey(MainActivity.this, pwdText.getText().toString());
                            }
                        } else {
                            pHandler.sendEmptyMessage(AsynTaskResult.FAILED.value());
                        }
                    }
                }).start();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_version:
                showVersionInfo();
                break;
            case R.id.action_settings:
                setServerAddr();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    /*
     * Reset the Server Address URL
     */
    private void setServerAddr() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setTitle("输入新Server地址：");
        builder.setIcon(android.R.drawable.ic_dialog_alert);

        final EditText newUrlText = new EditText(MainActivity.this);
        newUrlText.setText(KeyAccess.getServerURL(MainActivity.this));

        builder.setView(newUrlText);

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //refresh the server url info in Prefs
                KeyAccess.storeNewServerURL(MainActivity.this, newUrlText.getText().toString());
                Toast.makeText(MainActivity.this, "新Server地址更新成功...", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void showVersionInfo(){
        String versionNo = "0.1";
        try{
            PackageManager pm = getPackageManager();
            PackageInfo pi = pm.getPackageInfo(getPackageName(),0);
            versionNo = pi.versionName;
        }catch(PackageManager.NameNotFoundException nnfe){
            Log.e(TAG,"Cannot get Version name.");
        }

        new AlertDialog.Builder(MainActivity.this).setTitle("当前版本").setMessage("版本号为："+versionNo).setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
    }


}
