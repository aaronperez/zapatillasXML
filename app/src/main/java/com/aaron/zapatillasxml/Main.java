package com.aaron.zapatillasxml;

/**
 * Created by Aaron Perez on 20/11/2014.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.util.Xml;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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
    private int index;

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
            edicion(0, "add");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /***********Activities************/
    @Override
    public void onActivityResult(int requestCode,int resultCode, Intent data){
        if (resultCode== Activity.RESULT_OK) {
            switch (requestCode){
                case ACTIVIDAD_ELIMINAR:
                    index= data.getIntExtra("index",0);
                    zapas.remove(index);
                    break;
                case ACTIVIDAD_EDITAR:
                    index= data.getIntExtra("index",0);
                    String opcion=data.getStringExtra("opcion");
                    Zapatillas z=data.getParcelableExtra("zapa");
                    if(opcion.contains("edit")){
                        zapas.get(index).setModelo(z.getModelo());
                        zapas.get(index).setCaract(z.getCaract());
                        zapas.get(index).setPeso(z.getPeso());
                        zapas.get(index).setMarca(z.getMarca());

                    }
                    else{
                        zapas.add(z);
                    }
                    ordenar();
                    break;
            }
            try {
                escribir();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ad.notifyDataSetChanged();
        }
        else{
            Log.v("data",(data==null)+"");
            tostada(R.string.mensajeCancel);
        }
    }
    /********************Rotar*************************/
    /*
    @Override
    protected void onSaveInstanceState(Bundle savingInstanceState) {
        super.onSaveInstanceState(savingInstanceState);
        savingInstanceState.putParcelableArrayList("array", zapas);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        zapas=(savedInstanceState.getParcelableArrayList("array"));
    }*/

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
            edicion(index, "edit");
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

    //Ordena las zapatillas alfabéticamente
    public void ordenar(){
        Collections.sort(zapas);
    }

    /* Mostramos un mensaje flotante a partir de un recurso string*/
    private void tostada(int s){
        Toast.makeText(this, getText(s), Toast.LENGTH_SHORT).show();
    }


    /*        Menús          */
    /*************************/
    private final int ACTIVIDAD_ELIMINAR = 1;
    public void eliminar(int index){
        Intent i = new Intent(this,Eliminar.class);
        Bundle b = new Bundle();
        b.putString("mod", zapas.get(index).getModelo());
        b.putString("car", zapas.get(index).getCaract());
        b.putString("pes", zapas.get(index).getPeso());
        b.putInt("img", zapas.get(index).getMarca());
        b.putInt("index", index);
        i.putExtras(b);
        startActivityForResult(i, ACTIVIDAD_ELIMINAR);
    }

    private final int ACTIVIDAD_EDITAR = 2;
    public void edicion(int index,String opcion){
        Intent i = new Intent(this,Edicion.class);
        Bundle b = new Bundle();
        Zapatillas z;
        if(zapas.size()>0){
            z=zapas.get(index);
        }
        else{
            z=new Zapatillas();
        }
        b.putString("opcion",opcion);
        b.putInt("index", index);
        b.putParcelable("zapa",z);
        i.putExtras(b);
        startActivityForResult(i, ACTIVIDAD_EDITAR);
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
                }
                if(etiqueta.compareTo("caracteristicas")==0){
                    car = lectorxml.nextText();
                }
                if(etiqueta.compareTo("peso")==0){
                    pes = lectorxml.nextText();
                }
                if(etiqueta.compareTo("marca")==0){
                    mar = lectorxml.nextText();
                    zapas.add(new Zapatillas(mod, car, pes, Integer.parseInt(mar)));
                }
            }
            evento = lectorxml.next();
        }
    }

    /**
     * Created by Aaron Perez on 20/11/2014.
     */

    public static class Zapatillas implements Parcelable, Comparable<Zapatillas> {
        private String modelo, caract, peso;
        private int marca;

        public Zapatillas() {
        }

        public Zapatillas(String modelo, String caract, String peso, int marca) {
            this.modelo = modelo;
            this.caract = caract;
            this.peso = peso;
            this.marca = marca;
        }

        public Zapatillas(Parcel parcel){
            this.modelo = parcel.readString();
            this.caract = parcel.readString();
            this.peso = parcel.readString();
            this.marca =parcel.readInt();
        }

        public String getModelo() {
            return modelo;
        }

        public void setModelo(String modelo) {
            this.modelo = modelo;
        }

        public String getCaract() {
            return caract;
        }

        public void setCaract(String caract) {
            this.caract = caract;
        }

        public String getPeso() {
            return peso;
        }

        public void setPeso(String peso) {
            this.peso = peso;
        }

        public int getMarca() {
            return marca;
        }

        public void setMarca(int marca) {
            this.marca = marca;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Zapatillas)) return false;

            Zapatillas that = (Zapatillas) o;

            if (marca != that.marca) return false;
            if (!caract.equals(that.caract)) return false;
            if (!modelo.equals(that.modelo)) return false;
            if (!peso.equals(that.peso)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = modelo.hashCode();
            result = 31 * result + caract.hashCode();
            result = 31 * result + peso.hashCode();
            result = 31 * result + marca;
            return result;
        }

        @Override
        public int compareTo(Zapatillas zapatillas) {
            return this.getModelo().toLowerCase().compareTo(zapatillas.getModelo().toLowerCase());
        }

        @Override
        public int describeContents() {
            return 0;
        }

       @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(modelo);
            dest.writeString(caract);
            dest.writeString(peso);
            dest.writeInt(marca);
        }

        public static final Creator<Zapatillas> CREATOR =
                new Creator<Zapatillas>() {
                    public Zapatillas createFromParcel(Parcel parcel) {
                        return new Zapatillas(parcel);
                    }

                    public Zapatillas[] newArray(int size) {
                        return new Zapatillas[size];
                    }
                };

    }
}

