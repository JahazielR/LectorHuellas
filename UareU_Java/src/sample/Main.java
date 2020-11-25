package sample;

import com.digitalpersona.uareu.jni.Dpfpdd;
import com.digitalpersona.uareu.*;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;


class Lector {
    Reader r;
    Engine engine;
    byte[] datosCrudos;
    byte[] datosDesencriptados;

    public byte [] getBytesHuella (){
        return datosCrudos;
    }

    public void setBytesHuella (byte [] FMD){
        datosDesencriptados = FMD;
    }


    public Lector() throws UareUException,ArrayIndexOutOfBoundsException{
        ReaderCollection lectores = UareUGlobal.GetReaderCollection();
        int i = 0;
        while(lectores.size()==0&&i++<10){
            lectores.GetReaders();
        }
        r = lectores.get(0);
        engine = UareUGlobal.GetEngine();
    }

    public boolean validar(byte[] bytesFMD){
        try {
            r.Open(Reader.Priority.EXCLUSIVE);
            // Se crea un arreglo con datos minuciosos de la huella, arreglo de bytes proveniente de Base de Datos.
            Fmd fmd1 = UareUGlobal.GetImporter().ImportFmd(bytesFMD, Fmd.Format.ANSI_378_2004, Fmd.Format.ANSI_378_2004);
            System.out.println("Coleque su dedo para comporbar si exite en la Base de Datos");
            Fmd fmd2 = engine.CreateFmd(r.Capture(Fid.Format.ANSI_381_2004, Reader.ImageProcessing.IMG_PROC_DEFAULT,  500, -1).image, Fmd.Format.ANSI_378_2004);
            r.Close();
            //Dependiendo de lor requerimientos se deja cierto grado de dissimilitud en este caso 100
            return (engine.Compare(fmd2, 0, fmd1, 0)<100);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean validar(Fmd fmd2,byte[] patron){
        try {

            Fmd fmd1 = UareUGlobal.GetImporter().ImportFmd(patron, Fmd.Format.ANSI_378_2004, Fmd.Format.ANSI_378_2004);
            int vcomp = engine.Compare(fmd2, 0, fmd1, 0);
            System.out.println("Parecido de huella: "+vcomp);
            return vcomp<2000;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean comparacion( ){

        try {

            r.Open(Reader.Priority.EXCLUSIVE);
            System.out.println("Vuelva a posisionar el dedo para comprobar la huella");
            System.out.println(datosCrudos.toString() );
            String cadenaBytes = Base64.getEncoder().encodeToString( datosCrudos);

            datosDesencriptados = Base64.getDecoder().decode(cadenaBytes);
//
            int largo =  datosCrudos.length;
            System.out.println("El tamano de los bytes es de: " +  (largo -1));

            //Creación de FMD basado en un arreglo de bytes,
            //Fmd fmd1 = UareUGlobal.GetImporter().ImportFmd(data, Fmd.Format.ANSI_378_2004, Fmd.Format.ANSI_378_2004);

            Fmd fmd1 = UareUGlobal.GetImporter().ImportFmd(datosDesencriptados, Fmd.Format.ANSI_378_2004, Fmd.Format.ANSI_378_2004);
            Fmd fmd2 = engine.CreateFmd(r.Capture(Fid.Format.ANSI_381_2004, Reader.ImageProcessing.IMG_PROC_DEFAULT, 500, -1).image, Fmd.Format.ANSI_378_2004);
            int coinidencia_falsa = Engine.PROBABILITY_ONE / 100000;
            int puntaje_falso = engine.Compare(fmd2, 0, fmd1, 0);
            if(puntaje_falso < coinidencia_falsa){
                System.out.println("Huellas comparadas.\n");
                System.out.println("puntaje de dessimilitud: " + puntaje_falso);
            }
            else{
                System.out.println("Las huellas no coinciden.");
            }


            r.Close();
        } catch (UareUException ex) {
            ex.printStackTrace();
        }
            return false;
    }


    public Fmd capturar(){
        Fmd f = null;
        Fmd fmdBytes = null;

        try {
            r.Close();
        } catch (Exception e) {
        }
        try {
            r.Open(Reader.Priority.EXCLUSIVE);


            Fid imagen = null;
            while(imagen == null){
                System.out.println("Esperando que ponga su dedo en el lector.");
                imagen = r.Capture(Fid.Format.ANSI_381_2004, Reader.ImageProcessing.IMG_PROC_DEFAULT,  500, -1).image;
            }
            f = engine.CreateFmd(imagen, Fmd.Format.ANSI_378_2004);


            //Se consiguen el arreglo de bytes
            datosCrudos = f.getData();



/*
            System.out.println("Vuelva a posisionar el dedo para comprobar la huella");
            System.out.println(datosCrudos.toString() );
            String cadenaBytes = Base64.getEncoder().encodeToString( datosCrudos);
            //
            datosDesencriptados = Base64.getDecoder().decode(cadenaBytes);
//
            int largo =  datosCrudos.length;
            System.out.println("El tamano de los bytes es de: " +  (largo -1));

            //Creación de FMD basado en un arreglo de bytes,
            //Fmd fmd1 = UareUGlobal.GetImporter().ImportFmd(data, Fmd.Format.ANSI_378_2004, Fmd.Format.ANSI_378_2004);
            Fmd fmd1 = UareUGlobal.GetImporter().ImportFmd(datosDesencriptados, Fmd.Format.ANSI_378_2004, Fmd.Format.ANSI_378_2004);
            Fmd fmd2 = engine.CreateFmd(r.Capture(Fid.Format.ANSI_381_2004, Reader.ImageProcessing.IMG_PROC_DEFAULT, 500, -1).image, Fmd.Format.ANSI_378_2004);
            int coinidencia_falsa = Engine.PROBABILITY_ONE / 100000;
            int puntaje_falso = engine.Compare(fmd2, 0, fmd1, 0);
            if(puntaje_falso < coinidencia_falsa){
                System.out.println("Huellas comparadas.\n");
                System.out.println("puntaje de dessimilitud: " + puntaje_falso);
            }
            else{
                System.out.println("Las huellas no coinciden.");
            }

 */


            r.Close();
        } catch (UareUException ex) {
            ex.printStackTrace();
        }
        return f;







    }




    /*
    Reader reader = new Reader() {
        @Override
        public void Open(Priority priority) throws UareUException {

        }

        @Override
        public void Close() throws UareUException {

        }

        @Override
        public Status GetStatus() throws UareUException {
            return null;
        }

        @Override
        public Capabilities GetCapabilities() {
            return null;
        }

        @Override
        public Description GetDescription() {
            return null;
        }

        @Override
        public CaptureResult Capture(Fid.Format format, ImageProcessing imageProcessing, int i, int i1) throws UareUException {
            return null;
        }

        @Override
        public void CaptureAsync(Fid.Format format, ImageProcessing imageProcessing, int i, int i1, CaptureCallback captureCallback) throws UareUException {

        }

        @Override
        public void CancelCapture() throws UareUException {

        }

        @Override
        public void StartStreaming() throws UareUException {

        }

        @Override
        public void StopStreaming() throws UareUException {

        }

        @Override
        public CaptureResult GetStreamImage(Fid.Format format, ImageProcessing imageProcessing, int i) throws UareUException {
            return null;
        }

        @Override
        public void Calibrate() throws UareUException {

        }

        @Override
        public void Reset() throws UareUException {

        }
    }

     */


};


public class Main extends Application {




    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }




    public static void main(String[] args) {


        try {
            Fmd huella = null;
            Lector l = new Lector();
            System.out.println("Exito al crear el lector");
            huella = l.capturar();
            byte [] miHuella = l.datosCrudos;

            if (l.validar( miHuella))
            {
                System.out.println("Su huella coincide");
            }else{
                System.out.println("Su huella no coincide");
            }







        } catch (UareUException ex) {
            Logger.getLogger(Lector.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ArrayIndexOutOfBoundsException ex) {
            Logger.getLogger(Lector.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("El programa a finalizado");


        //launch(args);














    }
}
