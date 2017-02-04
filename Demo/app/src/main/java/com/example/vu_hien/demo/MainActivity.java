package com.example.vu_hien.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends Activity {

    //private String SDPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private String dataPath = null;
    private String inputFile = null;
    private String outputFile = null;
    long ratio;
    Button btnDecode, btnEncode;

    private static final int REQUEST_PATH = 1;

    String curFileName;

    EditText edittext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edittext = (EditText)findViewById(R.id.editText);

        btnDecode = (Button) findViewById(R.id.btnDecode);
        btnDecode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inputFile!=null){
                    if(!inputFile.endsWith(".huffman")){
                        Toast.makeText(MainActivity.this,"Invalid File!",Toast.LENGTH_LONG).show();
                    }else{
                        //Tạo Intent để mở ResultActivity
                        Intent myIntent=new Intent(MainActivity.this, ResultActivity.class);
                        long startuptime = System.currentTimeMillis();
                        DHuffman.setTree();
                        String temp = curFileName.substring(0,(curFileName.length()-8));
                        outputFile = dataPath + "decoded_" + temp;
                        if (DHuffman.decode(inputFile, outputFile)) {
                            String message = " Decode successfully." + "Took: "
                                    + (System.currentTimeMillis() - startuptime) + " miliseconds.";
                            myIntent.putExtra("message", message);
                            startActivity(myIntent);
                        }else{
                            Toast.makeText(MainActivity.this,"error",Toast.LENGTH_LONG).show();
                        }
                    }
                }else {
                    Toast.makeText(MainActivity.this,"Input file you want to decode",Toast.LENGTH_LONG).show();
                }
            }
        });


        btnEncode = (Button) findViewById(R.id.btnEncode);
        btnEncode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inputFile!=null){
                    //Tạo Intent để mở ResultActivity
                    Intent myIntent=new Intent(MainActivity.this, ResultActivity.class);
                    long startuptime = System.currentTimeMillis();
                    DHuffman.setTree();
                    outputFile = dataPath + curFileName + ".huffman";
                    if (DHuffman.encode(inputFile, outputFile)){
                        ratio = 100 - (new File(outputFile)).length() * 100 / (new File(inputFile)).length();
                        String message = " Encode successfully." +
                                "compress ratio %" + ratio + "Took: "
                                + (System.currentTimeMillis() - startuptime) + " miliseconds.";
                        myIntent.putExtra("message", message);
                        startActivity(myIntent);
                    }else{
                        Toast.makeText(MainActivity.this,"error",Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(MainActivity.this,"Input file you want to encode",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void getfile(View view){
        Intent intent1 = new Intent(this, FileChooser.class);
        startActivityForResult(intent1,REQUEST_PATH);
    }
    // Listen for results.
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        // See which child activity is calling us back.
        if (requestCode == REQUEST_PATH){
            if (resultCode == RESULT_OK) {
                curFileName = data.getStringExtra("GetFileName");
                edittext.setText(curFileName);
                dataPath = data.getStringExtra("GetPath")+"/";
                inputFile = dataPath + curFileName;
                //Toast.makeText(MainActivity.this,dataPath,Toast.LENGTH_LONG).show();
            }
        }
    }
}
