/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ia;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import processing.core.PApplet;
import processing.core.PFont;

import java.util.Random;

/**
 * Clase que muestra el laberinto gráficamente.
 * @author Emmanuel Cruz Hernández
 * @version Febrero 2019.
 */
public class TravellingProblem extends PApplet{

    PFont fuente;  // Fuente para mostrar texto en pantalla
    
    // Propiedades del modelo de la ventana.
    int alto = 15;         // Altura (en celdas) de la cuadricula.
    int ancho = 25;        // Anchura (en celdas) de la cuadricula.
    int celda = 40;          // Tamanio de cada celda cuadrada (en pixeles).
    Viaje model;  // El objeto que representa el modelo de caminos.
    
    @Override
    public void setup() {
        size( ancho*celda, (alto*celda)+32);
        background(50);
        fuente = createFont("Arial",12,true);
        model = new Viaje();
    }
    
    /**
     * Pintar el mundo del modelo (la cuadricula).
     */
    @Override
    public void draw() {
        stroke(0,0,0);
        fill(0,0,0);
        rect(0,0,ancho*celda,alto*celda);
        
        Camino c=model.mejorSolucion;
        
        int longitud=c.ciudades.size();
        
        City ciudad;
        City ciudad2;
        stroke(0,0,0);
        for(int k=0;k<longitud;k++){
            for(int l = 0; l < longitud; l++){
            ciudad=c.ciudades.get(k);
            ciudad2=c.ciudades.get(l);
            line((float)(ciudad.x), (float)(ciudad.y),
                 (float)(ciudad2.x), (float)(ciudad2.y));
            }
        }
        
        // Comienza a dibujar la solución del camino.
        
        stroke(0,255,0);
        fill(255,0,0);
        for(int i = 0; i < longitud; i++){
            ciudad=c.ciudades.get(i);
            rect((float)(ciudad.x/13)*645-700, (float)(ciudad.y)*-19 - 1650, 3, 3);
        }
        for(int j = 0; j < longitud-1; j++){
            ciudad=c.ciudades.get(j);
            ciudad2=c.ciudades.get(j+1);
            line((float)(ciudad.x/13)*645 - 700, (float)(ciudad.y)*-19 - 1650,
                 (float)(ciudad2.x/13)*645- 700, (float)(ciudad2.y)*-19 - 1650);
        }
        ciudad=c.ciudades.get(c.ciudades.size()-1);
        ciudad2=c.ciudades.get(0);
        line((float)(ciudad.x/13)*645-700, (float)(ciudad.y)*-19 - 1650,
             (float)(ciudad2.x/13)*645-700, (float)(ciudad2.y)*-19 - 1650);
        fill(50);
	rect(0, alto*celda, (ancho*celda), 32);
	fill(255);
        textFont(fuente,10);
	text("Cuadricula: " + ancho + " x " + alto, 5, (alto*celda)+12);
	text("Problema del Agente Viajero", 128, (alto*celda)+12);
        text("Aptitud del camino: "+c.aptitud, 328, (alto*celda)+12);
        text("Iteración: "+model.iteracion, 628, (alto*celda)+12);
        model.aplicaHopfield();
    }
        
    
    // --- Clase Celda ---
    class City {
    
	// Identificador.
	int id;

	// Coordenada x.
	double x;

	// Coordenada y.
	double y;

	/**
	 * Crea una nueva ciudad con identificador y coordenadas.
	 * @param id el identificador inicial a asignar.
	 * @param x la coordenada x de la ciudad en el plano.
	 * @param y la coordenada y de la ciudad en el plano.
	 */
	public City(int id, double x, double y){
	    this.id=id;
	    this.x=x;
	    this.y=y;
	}

	/**
	 * Calcula la distancia entre dos ciudades.
	 * @param otra la ciudad a encontrar la distancia.
	 */
	public double calculaDistancia(City otra){
	    return Math.sqrt(Math.pow(otra.x-this.x,2)+Math.pow(otra.y-this.y,2));
	}

	@Override
	public String toString(){
	    return ""+id;
	}
    }
    
    /**
     * Clase auxiliar que representa un camino de un viajero.
     */
    class Camino{

	/** Lista que contiene a las ciudades. */
	public ArrayList<City> ciudades;

	/** Aptitud del fenotipo. */
	public double aptitud;

	private Random rnd=new Random();

	/**
	 * Crea un nuevo Camino.
	 */
	public Camino(){
	    // Se leen las ciudades y se asignan.
	    ArrayList<City> leidas=readCities();

	    // Se inicializa la lista de ciudades para asignar.
	    ciudades=new ArrayList<>();

	    // Se llena el camino de ciudades. Esto es un nuevo camino.
	    while(!leidas.isEmpty()){
		City nueva=leidas.get(rnd.nextInt(leidas.size()));
		ciudades.add(nueva);
		leidas.remove(nueva);
	    }

	    this.calculaAptitud();
	}

	/**
	 * Crea un Camino a partir de una lista ya dada.
	 * @param fen el nuevo fenotipo a asignar al objeto.
	 */
	public Camino(ArrayList<City> fen){
	    ciudades=fen;
	    this.calculaAptitud();
	}

	/**
	 * Método que se encarga de leer el archivo que contiene información de las ciudades.
	 * @return lista con las ciudades leídas del archivo.
	 */
	public ArrayList<City> readCities(){
	    ArrayList<City> l=new ArrayList<>();
	    try{
		//BufferedReader lect=new BufferedReader(new FileReader("Datos/Rwanda.txt"));
		//BufferedReader lect=new BufferedReader(new FileReader("Datos/Zimbabwe.txt"));
		//BufferedReader lect=new BufferedReader(new FileReader("Datos/Djibouti.txt"));
                BufferedReader lect=new BufferedReader(new FileReader("Datos/small3.txt"));
		String cityInfo;
		while((cityInfo=lect.readLine())!=null){
		    int a=cityInfo.indexOf(" ");
		    int b=cityInfo.indexOf(" ",a+1);
		    int identificador=Integer.parseInt(cityInfo.substring(0,a));
		    double x=Double.parseDouble(cityInfo.substring(a+1,b));
		    double y=Double.parseDouble(cityInfo.substring(b+1,cityInfo.length()));
		    City nuevo=new City(identificador,x,y);
		    l.add(nuevo);
		}
	    }catch(FileNotFoundException e){
		System.out.println("\nERROR: El archivo no fue encontrado");
		return null;
	    }catch(IOException e){
		System.out.println("\nERROR: Algo salió mal al intentar leer archivo.");
		return null;
	    }
	    return l;
	}

	/**
	 * Calcula la aptitud del Camino.
	 * @return la distancia de recorrer un país con el orden de las ciudades del objeto.
	 */
	public double calculaAptitud(){
	    double contador=0;
	    // Se hace una suma de cada una de la distancia de cada una de las ciudades que son adyacentes.
	    for(int i=0;i<ciudades.size()-1;i++)
		contador+=ciudades.get(i).calculaDistancia(ciudades.get(i+1));
	    // Se suma la ultima con la primera ciudad.
	    contador+=ciudades.get(ciudades.size()-1).calculaDistancia(ciudades.get(0));
	    // Se asigna la suma de las distancias como aptitud.
	    aptitud=contador;
	    return contador;
	}

	/**
	 * Elimina una ciudad de una lista de ciudades. Se elimina por su identificador.
	 * @param lista la lista de ciudades en la cual eliminar.
	 * @param ciudad la ciudad a eliminar de la lista.
	 */
	private void eliminaCiudad(ArrayList<City> lista, City ciudad){
	    int tam=lista.size();
	    for(int i=0;i<tam;i++)
		if(lista.get(i).id==ciudad.id){
		    lista.remove(i);
		    return;
		}
	}

	@Override
	public String toString(){
	    String representacion="[ ";
            for (City ciudade : ciudades) {
                representacion += ciudade + " ";
            }
	    representacion+="]\n\n";

	    representacion+="Aptitud = "+aptitud+".\n";
	    return representacion;
	}

    }

    /**
     * Clase en la que se implementa el uso de los algoritmos genéticos
     * para resolver el problema del viajero.
     * @author Emmanuel Cruz Hernández. 314272588.
     * @version 0.1 Junio.
     */
    class Viaje{

        /** Mejor solución encontrada hasta el momento. */
        public Camino mejorSolucion;
        
        /** Iteracion. */
        public int iteracion;
        
        /**
         * Construye la representación de un nuevo viaje.
         */
        public Viaje(){
            mejorSolucion = new Camino();
            iteracion=100;
        }
        
        /**
         * Calcula un arreglo de pesos entre ciudades.
         */
        public double[][] calcD(ArrayList<City> ciudades){
            int n = ciudades.size();
            double[][] d = new double[n][n];
            for(int i = 0; i<n; i++)
                for(int j = 0; j<n; j++)
                    d[i][j] = ciudades.get(i).calculaDistancia(ciudades.get(j));
            return d;
        }
        
        /**
         * Aplica Hopfield
         */
        public Camino aplicaHopfield(){
            int n = mejorSolucion.ciudades.size();
            double[][] d = calcD(this.mejorSolucion.ciudades);
            Hopfield hp = new Hopfield(n, 50.0, d);
            Camino nuevo = toCamino(hp.predict(100, 100, 90, 100, 1, this.iteracion));
            mejorSolucion = mejorSolucion.aptitud<nuevo.aptitud? mejorSolucion: nuevo;
            iteracion+=10;
            return mejorSolucion;
        }
        
        /**
         * Convierte un arreglo de pesos en un nuevo camino.
         */
        public Camino toCamino(double[][] d){
            int[] camino = new int[d.length];
            for(int i = 0; i<camino.length;i++)
                camino[i] = -1;
            for(double[] renglon: d){
                for(double val: renglon){
                    if(val<0.5)
                        val = 0;
                    else
                        val = 1;
                    System.out.print(val+" ");
                }
                System.out.println();
            }
            for(int j = 0; j<d.length; j++)
                for(int i = 0; i<d.length; i++)
                    if(d[i][j]==1 && !isContained(camino, i))
                        camino[j] = i;
            for(int val2: camino)
                System.out.print(val2+" ");
            System.out.println("\n");
            for(int i=0; i<d.length; i++)
                if(camino[i]==-1)
                    camino[i] = findMax(camino, i);
            ArrayList<City> convertido =new ArrayList<>();
            for(int i = 0; i<camino.length; i++){
                System.out.print(camino[i]+" ");
                convertido.add(mejorSolucion.ciudades.get(camino[i]));
            }
            System.out.println("\n");
            return new Camino(convertido);
        }
        
        public int findMax(int[] camino, int pos){
            int val = pos;
            while(isContained(camino, val)){
                val = (val+1)%camino.length;
                System.out.println(val);
            }
            City esta = mejorSolucion.ciudades.get(pos);
            int min = val;
            City minC = mejorSolucion.ciudades.get(min);
            for(int i = 0; i<camino.length; i++){
                if(!isContained(camino, (min+i)%camino.length) && ((min+i)%camino.length)!=pos){
                    if(esta.calculaDistancia(mejorSolucion.ciudades.get((min+i)%camino.length)) <
                            esta.calculaDistancia(minC)){
                        min = (min+i)%camino.length;
                        minC = mejorSolucion.ciudades.get(min);
                    }
                }
            }
            return min;
        }
        
        private boolean isContained(int[] arreglo, int valor){
            for(int val:arreglo)
                if(val==valor)
                    return true;
            return false;
        }
    }
    
    /**
     * Clase que representa una red de Hopfield.
     * @author Sara Doris Montes Incin.
     * @author Emmanuel Cruz Hernández.
     * @version 1.0 Junio 2020.
     */
    class Hopfield{
        
        /** Cantidad de ciudades. */
	public int ciudades;

	/** Cantidad de neuronas. */
	public int neuronas;

	/** Caminos entre ciudades. */
	public double[][] d;

	/** Alpha. */
	public double alpha;
        
        /** Objeto Random. */
        private Random rn = new Random();
        
        /**
	* Crea una nueva red de Hopfield.
	* @param ciudades la cantidad de ciudades.
	* @param alpha el valor de alpha.
        * @param d los pesos
	*/
	public Hopfield(int ciudades, double alpha, double[][] d){
            this.ciudades = ciudades;
            this.neuronas = ciudades*ciudades;
            this.d = d;
            this.alpha = alpha;
	}
        
        /**
         * Función de para calcular pesos.
         * @param x elemento por el cual obtener el peso.
         */
        public double f(double x){
            return 0.5*(1.0 + Math.tanh(alpha*x));
        }
        
        /**
         * Entrena la red.
         */
        public double[][] train(double[][] u, double A, double B, double C, double D, int sigma){
            int n = ciudades;
            for(int iteration = 0; iteration < n*n; iteration++){
                int x = rn.nextInt(n-1);
                int i = rn.nextInt(n-1);
                double tmpA =0;
                for(int j = 0; j<n; j++)
                    if(i!=j)
                        tmpA += u[x][j];
                tmpA *= -1*A;
                double tmpB = 0;
                for(int y = 0; y<n; y++)
                    if(x!=y)
                        tmpB += u[y][i];
                tmpB *= -1*B;
                double tmpC = 0;
                for(int y = 0; y<n; y++)
                    for(int j = 0; j<n; j++)
                        tmpC += u[y][j];
                tmpC -= (n+sigma);
                tmpC *= -1*C;
                double tmpD = 0;
                for(int y = 0; y<n; y++)
                    if(0<i && i<n-1)
                        tmpD += this.d[x][y]*(u[y][i+1] + u[y][i-1]);
                    else if(i>0)
                        tmpD += this.d[x][y]*u[y][i-1];
                    else if(i<n-1)
                        tmpD += this.d[x][y]*u[y][i+1];
                tmpD *= -1*D;
                u[x][i] = this.f(tmpA + tmpB + tmpC + tmpD);
            }
            return u;
        }
        
        /**
         * Predice el entrenamiento de la red.
         */
        public double[][] predict(double A, double B, double C, double D, int sigma, int maxIter){
            double[][] u = new double[this.ciudades][this.ciudades];
            for(int i = 0; i<ciudades; i++)
                for(int j = 0; j<ciudades;j++)
                    u[i][j] = uniform(0, 0.03);
            double errorPrevio = calcError(u, A, B, C, D, sigma);
            int repeated = 0;
            int maxRepeated = 10;
            for(int iteration = 0; iteration<maxIter; iteration++){
                u = train(u, A, B, C, D, sigma);
                double error = calcError(u, A, B, C, D, sigma);
                if(error == errorPrevio)
                    repeated++;
                else
                    repeated = 0;
                
                if(repeated>maxRepeated)
                    break;
                errorPrevio = error;
            }
            return u;
        }
        
        /**
         * Calcula el error del entrenamiento.
         */
        public double calcError(double[][] u, double A, double B, double C, double D, int sigma){
            double tmpA = 0;
            int n = this.ciudades;
            for(int x = 0; x<n; x++)
                for(int i = 0; i<n; i++)
                    for(int j = 0; j<n; j++)
                        if(i!=j)
                            tmpA += u[x][i]*u[x][j];
            tmpA *= A/2.0;
            double tmpB = 0;
            for(int i = 0; i<n; i++)
                for(int x = 0; x<n; x++)
                    for(int y = 0; y<n; y++)
                        if(x!=y)
                            tmpB += u[x][i]*u[y][i];
            tmpB *= B/2.0;
            double tmpC = 0;
            for(int x = 0; x<n; x++)
                for(int i=0; i<n; i++)
                    tmpC += u[x][i];
            tmpC -= ((n+sigma)*(n+sigma));
            tmpC *= C/2.0;
            double tmpD = 0;
            for(int x = 0; x<n; x++)
                for(int y = 0; y<n; y++)
                    for(int i = 0; i<n; i++)
                        if(0<i && i<n-1)
                            tmpD = d[x][y]*u[x][i]*(u[y][i+1]+u[y][i-1]);
                        else if(i>0)
                            tmpD = d[x][y]*u[x][i]*u[y][i-1];
                        else if(i<n-1)
                            tmpD = d[x][y]*u[x][i]*u[y][i+1];
            tmpD *= D/2.0;
            return tmpA+tmpB+tmpC+tmpD;
        }
        
        private double uniform(double a, double b){
            double menor = a<b? a: b;
            double mayor = a>b? a: b;
            double valor = ((mayor-menor) * Math.random()) + menor;
            while(valor<menor || valor>mayor)
                valor = ((mayor-menor) * Math.random()) + menor;
            return valor;
        }
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
	PApplet.main(new String[] { "ia.TravellingProblem" });
    }
    
}
