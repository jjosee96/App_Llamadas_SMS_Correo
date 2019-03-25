package com.example.jose.myapplication;


import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.RestrictionEntry;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

import static android.provider.ContactsContract.*;

public class MainActivity extends AppCompatActivity {

    EditText txtmensaje;
    Button btnEnviarMensaje;

    private final int PICK_CONTACT_REQUEST = 1;
    private final int PERMISSIONS_REQUEST_READ_CONTACT = 100;
    private final int PERMISSIONS_REQUEST_CALL_PHONE = 101;
    private final int PERMISSIONS_REQUEST_READ_SMS = 102;
    private final int PERMISSIONS_REQUEST_RECIVE_SMS = 103;
    private Uri contactoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new
                            String[]{Manifest.permission.READ_CONTACTS},
                    PERMISSIONS_REQUEST_READ_CONTACT
            );
        }

        if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.SEND_SMS)!=
                PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.SEND_SMS)){
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS},1);
            }else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS},1);
            }
        }


        if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.CALL_PHONE)!=
                PackageManager.PERMISSION_GRANTED){
            requestPermissions(new

                            String[]{Manifest.permission.CALL_PHONE},
                    PERMISSIONS_REQUEST_CALL_PHONE
            );
        }


        if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.RECEIVE_SMS)!=
                PackageManager.PERMISSION_GRANTED){

            requestPermissions(new
                            String[]{Manifest.permission.RECEIVE_SMS},
                    PERMISSIONS_REQUEST_RECIVE_SMS
            );

        }

        if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_SMS)!=
                PackageManager.PERMISSION_GRANTED){

            requestPermissions(new
                            String[]{Manifest.permission.READ_SMS},
                    PERMISSIONS_REQUEST_READ_SMS
            );

        }





        txtmensaje = (EditText)findViewById(R.id.txtMensaje);
        btnEnviarMensaje =(Button)findViewById(R.id.botonEnviarSMS);


    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_CONTACT:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    return;
                } else {
                    finish();
                }
                return;

            case PERMISSIONS_REQUEST_CALL_PHONE:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    return;
                } else {
                    finish();
                }
                return;
        }
    }

    public void initSeleccionarContacto(View v) {
        Intent i = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
        startActivityForResult(i, PICK_CONTACT_REQUEST);
    }

    private void recibirConctacto(Uri uri) {
        TextView nombre = (TextView) findViewById(R.id.nombreContacto);
        TextView telefono = (TextView) findViewById(R.id.telefonoContacto);
        ImageView foto = (ImageView) findViewById(R.id.fotoContacto);
        TextView correo = (TextView) findViewById(R.id.Correo);


        nombre.setText(getNombre(uri));
        telefono.setText(getTelefono(uri));
        foto.setImageBitmap(getFoto(uri));
        correo.setText(getMail(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_CONTACT_REQUEST) {
            if (resultCode == RESULT_OK) {
                contactoUri = data.getData();

                recibirConctacto(contactoUri);
            }
        }
    }

    private String getNombre(Uri uri) {
        String nombre = null;

        ContentResolver contentResolver = getContentResolver();

        Cursor c = contentResolver.query(
                uri,
                new String[]{Contacts.DISPLAY_NAME},
                null,
                null,
                null
        );

        if (c.moveToFirst()) {
            nombre = c.getString(0);
        }
        c.close();

        return nombre;
    }

    private String getMail(Uri uri) {
        String correo = null;
        String id = null;
        Cursor contactoCursor = getContentResolver().query(
                uri,
                new String[]{Contacts._ID},
                null,
                null,
                null
        );

        if (contactoCursor.moveToFirst()){
            id = contactoCursor.getString(0);
        }

        contactoCursor.close();

        String selectionArgs =
                CommonDataKinds.Email.CONTACT_ID + "= ?";
        Cursor CorreoCursor = getContentResolver().query(
                CommonDataKinds.Email.CONTENT_URI,
                new String[] {CommonDataKinds.Email.DATA1},
                selectionArgs,
                new String[]{id},
                null
        );
        if (CorreoCursor.moveToFirst()){
            correo = CorreoCursor.getString(0);
        }

        CorreoCursor.close();

        return correo;
    }


    private String getTelefono(Uri uri) {
        String id = null;
        String telefono = null;

        Cursor contactoCursor = getContentResolver().query(
                uri,
                new String[]{Contacts._ID},
                null,
                null,
                null
        );

        if (contactoCursor.moveToFirst()){
            id = contactoCursor.getString(0);
        }

        contactoCursor.close();

        String selectionArgs =
                CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                        CommonDataKinds.Phone.TYPE + " = " +
                        CommonDataKinds.Phone.TYPE_MOBILE;

        Cursor telefonoCursor = getContentResolver().query(
                CommonDataKinds.Phone.CONTENT_URI,
                new String[] {CommonDataKinds.Phone.NUMBER},
                selectionArgs,
                new String[]{id},
                null
        );
        if (telefonoCursor.moveToFirst()){
            telefono = telefonoCursor.getString(0);
        }

        telefonoCursor.close();

        return telefono;
    }

    private Bitmap getFoto(Uri uri){
        Bitmap foto = null; String id = null;
        Cursor contactoCursor = getContentResolver().query(
                uri,
                new String[]{Contacts._ID},
                null,null,null
        );
        if (contactoCursor.moveToFirst()){
            id = contactoCursor.getString(0);
        }
        contactoCursor.close();
        try{
            Uri contacctoUri = ContentUris.withAppendedId(
                    Contacts.CONTENT_URI,
                    Long.parseLong(id)
            );
            InputStream input = Contacts.openContactPhotoInputStream(
                    getContentResolver(),
                    contacctoUri
            );
            if (input != null) {
                foto = BitmapFactory.decodeStream(input);
                input.close();
            }
        }catch (IOException ioe){ }
        return foto;
    }



    public void sendMessage(View v){

        /*
        Creando nuestro gestor de mensajes
         */
        SmsManager smsManager = SmsManager.getDefault();

        /*
        Enviando el mensaje
         */
        if(contactoUri!=null) {
            smsManager.sendTextMessage(
                    getTelefono(contactoUri),
                    null,
                    "¡Estamos aprendiendo a Desarrollar en Android!",
                    null,
                    null);

            Toast.makeText(this, "Mensaje Enviado", Toast.LENGTH_LONG).show();
        }else
            Toast.makeText(this, "Selecciona un contacto primero", Toast.LENGTH_LONG).show();


    }

    public void EnviarMensaje(View v){


        TextView telefono = (TextView) findViewById(R.id.telefonoContacto);

         String numeTelefono = telefono.getText().toString();
         String mensaje = txtmensaje.getText().toString();


        try{
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(numeTelefono, null, mensaje, null,null);
            Toast.makeText(getApplicationContext(), "Mensaje Enviado.", Toast.LENGTH_LONG).show();
            txtmensaje.getText().clear();
        }

        catch (Exception e){
            Toast.makeText(getApplicationContext(), "Mensaje no enviado, datos incorrectos." + e.getMessage().toString(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }

    public void llamar(View v){
        TextView telefono = (TextView) findViewById(R.id.telefonoContacto);
        try{

            String numero = telefono.getText().toString();
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:"+numero));
            startActivity(intent);

        }catch (Exception e){
            Toast.makeText(MainActivity.this,"Algo salio mal. Intentelo de nuevo",Toast.LENGTH_LONG).show();
        }


    }

    TextView correo;
    protected void enviar_Email(View view) {

        try{
           correo  = (TextView) findViewById(R.id.Correo);
           if (correo.equals(null)){
               Toast.makeText(MainActivity.this, "Campo de correo vacio",Toast.LENGTH_LONG).show();
           }else{

               String mensaje = txtmensaje.getText().toString();
               String email = correo.getText().toString();
               String[] TO = {email}; //aquí pon tu correo
               String[] CC = {""};
               Intent emailIntent = new Intent(Intent.ACTION_SEND);
               emailIntent.setData(Uri.parse("mailto:"));
               emailIntent.setType("text/plain");
               emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
               emailIntent.putExtra(Intent.EXTRA_CC, CC);


               /// emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Asunto");
               emailIntent.putExtra(Intent.EXTRA_TEXT, mensaje);

               try {
                   startActivity(Intent.createChooser(emailIntent, "Enviar email..."));
                   finish();
                   txtmensaje.getText().clear();
               } catch (android.content.ActivityNotFoundException ex) {
                   Toast.makeText(MainActivity.this,
                           "No tienes clientes de email instalados/Selecciona un contacto con email", Toast.LENGTH_SHORT).show();
               }

           }
        }catch (Exception e){


        }

    }

}




