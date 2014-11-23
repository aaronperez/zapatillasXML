package com.aaron.zapatillasxml;

/**
 * Created by Aaron Perez on 20/11/2014.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;


public class Main extends Activity {

    private ArrayList<Zapatillas> zapas = new ArrayList<Zapatillas>();
    private ListView lv;
    private Adaptador ad;
    private Spinner spinner;
    private int img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            initComponents();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_agregar) {
            editar(0, "add");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /***********Activities************/
    @Override
    public void onActivityResult(int requestCode,int resultCode, Intent data){
        if (resultCode== Activity.RESULT_OK) {
            switch (requestCode){
                case ACTIVIDAD_SEGUNDA:
                    int index= data.getIntExtra("index",0);
                    zapas.remove(index);
                    ad.notifyDataSetChanged();
                    break;
            }
        }
        else{
            Log.v("data",(data==null)+"");
            tostada(R.string.mensajeCancelEliminar);
        }
    }
    /********************Rotar*************************/
    @Override
    protected void onSaveInstanceState(Bundle savingInstanceState) {
        super.onSaveInstanceState(savingInstanceState);
        savingInstanceState.putParcelableArrayList("array", zapas);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        zapas=(savedInstanceState.getParcelableArrayList("array"));
    }

    /* Desplegar menú contextual*/
    @Override
    public void onCreateContextMenu(ContextMenu main, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(main, v, menuInfo);
        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.contextual, main);
    }

    /* Al seleccionar elemento del menú contextual */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int id=item.getItemId();
        AdapterView.AdapterContextMenuInfo info= (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int index= info.position;
        if (id == R.id.action_editar) {
            editar(index,"edit");
            return true;
        }else if (id == R.id.action_eliminar) {
            eliminar(index);
            return true;
        }
        return super.onContextItemSelected(item);
    }

    /*         Auxiliares        */
    /*****************************/

    private void initComponents() throws IOException, XmlPullParserException {
        //nuevas();
        ad = new Adaptador(this, R.layout.elemento, zapas);
        lv = (ListView) findViewById(R.id.lvLista);
        lv.setAdapter(ad);
        registerForContextMenu(lv);
        leer();
    }

    /*  Método para iniciar Spinner y escucharlo  */
    private void iniciarSpinner(View v){
        ArrayAdapter<CharSequence> stringArrayAdapter=ArrayAdapter.createFromResource(this,R.array.Marca,android.R.layout.simple_spinner_dropdown_item);
        spinner =(Spinner)v.findViewById(R.id.spinnerM);
        spinner.setAdapter(stringArrayAdapter);
    }


    /* Método que convierte la posición en
    * el spinner en la imagen que le corresponde*/
    private int spinnerDraw(){
        Context n=Main.this.getApplicationContext();
        Resources resources = n.getResources();
        String nombre=spinner.getSelectedItem().toString().toLowerCase();
        final int resourceId = resources.getIdentifier(prepararNombre(nombre), "drawable",
                n.getPackageName());
        return resourceId;
        }
    /*
    //rellenamos algunos elementos del ArrayList para que se vea algo
    private void nuevas(){
        zapas.add(new Zapatillas("Adidas Riot 5","Montaña, pronador","70-90",R.drawable.adidas));
        zapas.add(new Zapatillas("Asics Gel Pulse 6","Asfalto, pronador","65-75", R.drawable.asics));
        zapas.add(new Zapatillas("Nike Pegasus","Asfalto, pronador","60-80",R.drawable.nike));
        zapas.add(new Zapatillas("New Balance 770 v4","Asfalto, pronador/neutro","70-75",R.drawable.newbalance));
        zapas.add(new Zapatillas("Mizuno Wave Ultima 6","Asfalto, neutro","70-80",R.drawable.mizuno));
        zapas.add(new Zapatillas("Saucony Virrata 2","Asfalto, neutro, drop 0","60-80", R.drawable.saucony));
    }
    */

    /* Mostramos un mensaje flotante a partir de un recurso string*/
    private void tostada(int s){
        Toast.makeText(this, getText(s), Toast.LENGTH_SHORT).show();
    }

    //Ordena las zapatillas alfabéticamente
    public void ordenar(){
        Collections.sort(zapas);
    }

    //Recoge el nombre del spinner y lo deja sin espacios
    public String prepararNombre(String n){
        if(n.contains(" ")){
            n=n.substring(0,n.indexOf(" "))+n.substring(n.indexOf(" ")+1,n.length());
        }
        return n;
    }


    /*        Menús          */
    /*************************/
    public void editar(int x,String opcion){
        final int x2=x;
        AlertDialog.Builder alert= new AlertDialog.Builder(this);
        LayoutInflater inflater= LayoutInflater.from(this);
        final View vista = inflater.inflate(R.layout.edicion, null);
        alert.setView(vista);
        iniciarSpinner(vista);
        final EditText et1=(EditText)vista.findViewById(R.id.etModelo);
        final EditText et2=(EditText)vista.findViewById(R.id.etUsos);
        final EditText et3=(EditText)vista.findViewById(R.id.etPeso);
        if(opcion.contains("edit")) {
            alert.setTitle(getText(R.string.tituloEditar));
            et1.setText(zapas.get(x2).getModelo());
            et2.setText(zapas.get(x2).getCaract());
            et3.setText(zapas.get(x2).getPeso());
            for (int i = 0; i < 7; i++) {
                if (spinner.getItemAtPosition(i).toString().substring(0, 3).equals(zapas.get(x2).getModelo().substring(0, 3))) {
                    spinner.setSelection(i);
                    break;
                }
            }
            zapas.get(x2).getModelo().substring(0, 3);
            alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    zapas.get(x2).setModelo(et1.getText().toString());
                    zapas.get(x2).setMarca(spinnerDraw());
                    zapas.get(x2).setCaract(et2.getText().toString());
                    zapas.get(x2).setPeso(et3.getText().toString());
                    img=spinnerDraw();
                    zapas.get(x2).setMarca(img);
                    ordenar();
                    ad.notifyDataSetChanged();
                    tostada(R.string.mensajeElementoEditado);
                    try {
                        escribir();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        else{
            alert.setTitle(getText(R.string.tituloAgregar));
            alert.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    int img=spinnerDraw();
                    String c= spinner.getSelectedItem().toString()+" "+et1.getText().toString();
                    zapas.add(new Zapatillas(c,et2.getText().toString(),et3.getText().toString(),img));
                    ordenar();
                    ad.notifyDataSetChanged();
                    tostada(R.string.mensajeElementoAdd);
                    try {
                        escribir();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        alert.setNegativeButton(getText(R.string.cancelar),null);
        alert.show();
    }


    private final int ACTIVIDAD_SEGUNDA = 1;
    public void eliminar(int index){
        Intent i = new Intent(this,Eliminar.class);
        Bundle b = new Bundle();
        b.putString("mod", zapas.get(index).getModelo());
        b.putString("car", zapas.get(index).getCaract());
        b.putString("pes", zapas.get(index).getPeso());
        b.putInt("img", zapas.get(index).getMarca());
        b.putInt("index", index);
        i.putExtras(b);
        startActivityForResult(i, ACTIVIDAD_SEGUNDA);
    }

    /********XML***********/
    public void escribir() throws IOException {
        File f = new File(getExternalFilesDir(null), "zapatillas.xml");
        FileOutputStream fosxml = new FileOutputStream(f);
        XmlSerializer docxml = Xml.newSerializer();
        docxml.setOutput(fosxml, "UTF-8");
        docxml.startDocument(null, Boolean.valueOf(true));
        docxml.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        docxml.startTag(null, "raiz");
        for(Zapatillas z : zapas){
            docxml.startTag(null, "zapatilla");
                docxml.startTag(null, "modelo");
                docxml.text(z.getModelo());
                docxml.endTag(null, "modelo");
                docxml.startTag(null, "caracteristicas");
                docxml.text(z.getCaract());
                docxml.endTag(null, "caracteristicas");
                docxml.startTag(null, "peso");
                docxml.text(z.getPeso());
                docxml.endTag(null, "peso");
                docxml.startTag(null, "marca");
                docxml.text(String.valueOf(z.getMarca()));
                docxml.endTag(null, "marca");
            docxml.endTag(null, "zapatilla");
        }
        docxml.endDocument();
        docxml.flush();
        fosxml.close();
    }

    public void leer() throws IOException, XmlPullParserException {
        XmlPullParser lectorxml= Xml.newPullParser();
        lectorxml.setInput(new FileInputStream(new File(getExternalFilesDir(null),"zapatillas.xml")),"utf-8");
        int evento = lectorxml.getEventType();
        String mod="",car="",pes="",mar="";
        while(evento != XmlPullParser.END_DOCUMENT){
            if(evento == XmlPullParser.START_TAG){
                String etiqueta = lectorxml.getName();
                if(etiqueta.compareTo("zapatilla")==0){}
                if(etiqueta.compareTo("modelo")==0){
                    mod = lectorxml.nextText();
                    Log.v("mod",mod);
                }
                if(etiqueta.compareTo("caracteristicas")==0){
                    car = lectorxml.nextText();
                    Log.v("car",car);
                }
                if(etiqueta.compareTo("peso")==0){
                    pes = lectorxml.nextText();
                    Log.v("pes", pes);
                }
                if(etiqueta.compareTo("marca")==0){
                    mar = lectorxml.nextText();
                    zapas.add(new Zapatillas(mod, car, pes, Integer.parseInt(mar)));
                }
            }
            evento = lectorxml.next();
        }
    }
}

