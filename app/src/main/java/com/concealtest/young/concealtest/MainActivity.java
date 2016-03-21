package com.concealtest.young.concealtest;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.crypto.keychain.SharedPrefsBackedKeyChain;
import com.facebook.crypto.Crypto;
import com.facebook.crypto.Entity;
import com.facebook.crypto.util.SystemNativeCryptoLibrary;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private static final String FILE_NAME = "decode";

    private Crypto mCrypto;
    private TextView mTextView;
    private EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCrypto = new Crypto(new SharedPrefsBackedKeyChain(this), new SystemNativeCryptoLibrary());

        // TODO
        findViewById(R.id.btn_encode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                encode();
            }
        });

        findViewById(R.id.btn_decode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decode();
            }
        });

        mTextView = (TextView) findViewById(R.id.tv_output);
        mEditText = (EditText) findViewById(R.id.editTx_input);
    }

    private void encode() {
        try {
            String content = mEditText.getText().toString();
            File dest = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/concealTest");
            if (!dest.exists()) {
                dest.mkdirs();
            }

            File file = new File(dest, FILE_NAME);
            if (!file.exists()) {
                file.createNewFile();
            }
            OutputStream fileStream = new BufferedOutputStream(new FileOutputStream(file));

            OutputStream outStream = mCrypto.getCipherOutputStream(fileStream, new Entity(FILE_NAME));
            outStream.write(file2byte(Environment.getExternalStorageDirectory().getAbsolutePath() + "/concealTest/234.jpg"));
            outStream.flush();
            outStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void decode() {
        File dest = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/concealTest/" + FILE_NAME);
        if (!dest.exists()) {
            Toast.makeText(this, "File is not avaiable", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            FileInputStream fileStream = new FileInputStream(dest);
            InputStream inputStream = mCrypto.getCipherInputStream(fileStream, new Entity(FILE_NAME));


            ByteArrayOutputStream out = new ByteArrayOutputStream();

            int read;
            byte[] buffer = new byte[1024];

            while ((read = inputStream.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }

            inputStream.close();

            String desPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/concealTest";
            byte2File(out.toByteArray(), desPath, "decode2.jpg");

            out.close();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Decode error", Toast.LENGTH_SHORT).show();
        }
    }

    public static byte[] file2byte(String filePath) {
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    public static void byte2File(byte[] buf, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(filePath);
            if (!dir.exists() && dir.isDirectory()) {
                dir.mkdirs();
            }
            file = new File(filePath + File.separator + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(buf);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

