import javax.swing.*; 
import javax.swing.filechooser.FileNameExtensionFilter;  
import java.awt.event.*; 
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.*;
import java.io.*; 
import java.util.List;
import java.util.ArrayList; 
import javax.swing.JLabel;
import java.net.URL;
import java.util.Arrays;
import java.awt.Font;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Random;
 
public class FileChooserGPX extends JFrame implements ActionListener{    
		JMenuBar mb; //declar o variabilă de tip JMenuBar care va fi utilizată pentru a crea o bara de meniu   
		JMenu file;   // declar o variabilă de tip JMenu  care va fi utilizată pentru a crea un meniu 
		JMenuItem open;   //declar o variabilă de tip JMenuItem care va fi utilizată pentru a crea un element de meniu 
		JTextArea ta; //declar o variabilă de tip JTextArea care va fi utilizată pentru a crea o zonă de text
		DEM dem;//declar o variabilă de tip DEM care va fi utilizată pentru calcularea elevației
		private int mouseX;
		private int mouseY;//declar variabile mouseX și mouseY pentru metodele mousePressed și mouseClicked
		private boolean mousePressed;//declar o variabilă boolean pentru verificarea apăsării mouse-ului
		private MyNewDialog dialog;//declar o variabilă de tip MyNewDialog pentru a 
		//afișa ferestra în care conțin informații despre pantă, ascedent, descedent și informații generale despre utilizator 
		//cum ar fi genul, vârsta,etc și chiar calorii arse
		private Calories calorieCalculator = new Calories();//declar o variabilă de tip Calories pentru calcularea caloriilor
		String gender = "female";
		int age = 22;
		double kilograme1 = 60;
		double inaltime1 = 162;
		double backpack = 22;
		double VO2max = 40;						
		double AHR = 90;											

		FileChooserGPX(){   
			
			open=new JMenuItem("Open File");   //creează un obiect JMenuItem cu eticheta "Open File" și îl atribuie variabilei open
			open.addActionListener(this);   //adauga un ascultator de evenimente la elementul de meniu 'open',care va fi gestionat
					
										//  in clasa FileChooserGPX (deoarece implementeaza 'ActionListener')
			file=new JMenu("File");    //creeaza un obiect JMenu cu eticheta "File" si il atribuie variabilei 'file'.
			file.add(open);            //adaug elementul de meniu 'open' la meniul file.
			mb=new JMenuBar();    		//creeaza un obiect JMenuBar si il atribuie variabilei 'mb'
			mb.setBounds(0,0,800,20);    //seteaza dimensiunile si pozitia barei de meniu
			mb.add(file);              	//adaugă meniul 'file' la bara de meniu 'mb'
			ta=new JTextArea(50,50); 	//creeaza un obiect JTextArea cu dimensiuni de 50x50 si il atribuie variabilei 'ta'
			ta.setEditable(false);		//setează proprietatea 'editable' a zonei de text ta pe false, ceea ce înseamnă că 
										//utilizatorul nu poate modifica continutul acesteia
			ta.setBounds(0,20,800,800);	//setează dimensiunile si pozitia zonei de text 'ta'
			add(mb);    				//adaugă bara de meniu 'mb' la componenta curenta 
			add(ta);              		//adaugă zona de text 'ta' la componenta curenta
		}    
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == open) {
				JFileChooser fc = new JFileChooser();
				//creează un obiect JFileChooser, care permite utilizatorului să selecteze un fișier din sistemul de fișiere
				fc.setFileFilter(new FileNameExtensionFilter("GPX Files", "gpx"));
				fc.addChoosableFileFilter(new FileNameExtensionFilter("KML Files", "kml")); // Adaugă filtrul pentru fișiere KML
				//setează un filtru de fișiere pentru JFileChooser pentru a permite utilizatorului să selecteze doar fișiere cu extensia ".gpx" (fișiere GPX). Acest filtru este afișat în dialogul de deschidere a fișierului
				int i = fc.showOpenDialog(this);
				//afișează dialogul de deschidere a fișierului și așteaptă ca utilizatorul să selecteze un fișier sau să închidă dialogul. Rezultatul este stocat în variabila 'i'
				if (i == JFileChooser.APPROVE_OPTION) { //verifică dacă utilizatorul a selectat un fișier și nu a anulat dialogul
					File f = fc.getSelectedFile();
					//obține fișierul selectat de utilizator din 'JFileChooser' și îl stochează în variabila f de tip 'File'
					String filepath = f.getPath(); //obține calea absolută a fișierului selectat și o stochează în variabila 'filepath' de tip 'String'
					
					String fileExtension = getFileExtension(filepath);
					if (fileExtension.equals("gpx")) {
						// Fișierul este GPX
						try {
							BufferedReader br = new BufferedReader(new FileReader(filepath));//creează un obiect 'BufferedReader' pentru a citi conținutul fișierului selectat
																							//. Acesta primește un obiect 'FileReader' care citește din fișierul specificat prin filepath														
							//Se declară câteva variabile necesare pentru procesarea conținutului fișierului
							String line; // Variabila în care se stochează fiecare linie citită din fișier
							String[] trkptLines = new String[100000]; // Un array de string-uri în care se vor stoca liniile care conțin "<trkpt"
							String[] eleLines = new String[100000]; // Un array de string-uri în care se vor stoca liniile care conțin "<ele"
							int line1_len = 0; // Variabila ce memorează numărul de linii care conțin "<trkpt"
							//Parcurg fiecare linie din fișier folosind bucla while. Citirea continuă până când br.readLine()
							//returnează null, ceea ce înseamnă că s-au citit toate liniile din fișier
							while ((line = br.readLine()) != null) {
								// Verifică dacă linia conține "<trkpt" și, în caz afirmativ, o adaugă în array-ul 'trkptLines'
								if (line.contains("<trkpt")) {
									trkptLines[line1_len] = line;
									line1_len++;
								}
								// Verifică dacă linia conține "<ele" și, în caz afirmativ, o adaugă în array-ul 'eleLines'
								if (line.contains("<ele")) {
									eleLines[line1_len] = line;
								}
							}
							//Declar și inițializez array-urile necesare pentru a stoca valorile latitudinilor, longitudinilor și altitudinilor
							double[] latitudine = new double[line1_len];
							double[] longitudine = new double[line1_len];
							double[] elevatia = new double[line1_len];
							//parcurg fiecare element din array-ul trkptLines și extrag valorile latitudinilor și longitudinilor
							for (int j = 0; j < line1_len; j++) {
								String temp = trkptLines[j]; // Linia curentă din array
								String[] temp_output = temp.split(" "); // Se împarte linia în cuvinte folosind spațiul ca separator
								for (int z = 0; z < temp_output.length; z++) {
									// Verifică dacă cuvântul începe cu "lat" și extrage valoarea latitudinii
									if (temp_output[z].startsWith("lat")) {
										String value = temp_output[z].substring(5, temp_output[z].length() - 1);
										latitudine[j] = Double.parseDouble(value);
									}
									// Verifică dacă cuvântul începe cu "lon" și extrage valoarea longitudinii
									if (temp_output[z].startsWith("lon")) {
										String value = temp_output[z].substring(5, temp_output[z].length() - 2);
										longitudine[j] = Double.parseDouble(value);
									}
								}
							}

							
							for(int j = 2;j < line1_len;j++){	
									//Variabilele lat și lon sunt inițializate cu valoarea întreagă a latitudinii și longitudinii punctului curent (latitudine[j] și longitudine[j]). Aceste valori vor fi folosite pentru a determina numele fișierului DEM						
									int lat = (int)latitudine[j];
									int lon = (int)longitudine[j];
									double maxlat = lat+1;
									double maxlon = lon+1;		
									if(maxlat<=latitudine[j]) lat++;
									if(maxlon<=longitudine[j]) lon++;
									//creez un șir de caractere name care conține numele fișierului DEM format din latitudine și longitudine
									String name = "n0"+lat+"e0"+lon;
									//Se verifică dacă obiectul dem (DEM - Digital Elevation Model) este null. Dacă da, se creează un nou obiect DEM cu numele name. Dacă nu, se verifică dacă numele obiectului DEM existent nu începe cu name. În acest caz, se închide obiectul DEM curent și se creează un nou obiect DEM cu numele name								
									if(dem==null) dem = new DEM(name);
									else if(!dem.name.startsWith(name)){
										dem.close();
										dem = new DEM(name);
									}
									//Valoarea altitudinii returnată de getElevation() este asignată în array-ul elevatia la indicele j
									elevatia[j] = dem.getElevation(longitudine[j], latitudine[j]);
							}
							
							// for(int j = 2;j < line1_len;j++){
								// ta.append("lat : " + latitudine[j] + " ");
								// ta.append("lon : " + longitudine[j] + " ");
								// ta.append("elevatie1 : " + elevatia[j] + " ");
								// ta.append("\n");
							// }
							
							//Declar și inițializez un array de numere reale distances cu o lungime de line1_len - 1. Acest array va fi folosit pentru a stoca distanțele calculate
							double[] distances = new double[line1_len - 1];
							
							for (int a = 2; a < line1_len - 1; a++) {
								//Se parcurge fiecare element din intervalul [2, line1_len - 1]. Aceasta presupune că se calculează distanțele între cel de-al doilea și penultimul punct din array-urile latitudine și longitudine.
								//În fiecare iterație, se calculează diferența de latitudine și diferența de longitudine dintre punctul curent și următorul punct. Aceste valori sunt convertite în radiani folosind Math.toRadians().
								double dLat = Math.toRadians(latitudine[a+1] - latitudine[a]);
								double dLon = Math.toRadians(longitudine[a+1] - longitudine[a]);
						 
								final int R = 6371; // raza pământului în kilometri
								//Formula Haversine este utilizată pentru a calcula distanța pe sferă dintre cele două puncte, folosind diferențele de latitudine și longitudine. Valorile sunt stocate în variabila har.
								double har = Math.sin(dLat / 2) * Math.sin(dLat / 2)
										+ Math.cos(Math.toRadians(latitudine[a])) * Math.cos(Math.toRadians(latitudine[a+1]))
										* Math.sin(dLon / 2) * Math.sin(dLon / 2);
								// reprezintă un unghi central calculat pe baza valorii har
								double c = 2 * Math.atan2(Math.sqrt(har), Math.sqrt(1 - har));
								//Rezultatul este stocat în array-ul distances, la poziția corespunzătoare iterării curente
								distances[a] = R * c * 1000;
							}
							
							double sum = 0;
							for (int a = 2; a < line1_len - 1; a++) {
								sum += distances[a];
							}
							//System.out.println(sum + "\n");
							final double sumDist = sum;
							
							// for (int a = 2; a < latitudine.length - 1; a++) {
								// System.out.println(distances[a] + "\n");
							// }
							// int n = (int) sumDist;
							// double[] dist = new double[(2*n)+1];
							// double index = 0;
							
							// for (int a = 0; a < (2*n)+1; a ++) {
								// dist[a] = index;
								// System.out.println(dist[a] + "\n");
								// index += 0.5;
								
							// } 
							double max1 = elevatia[0];
							double min1 = elevatia[0];
							for (int b = 1; b < elevatia.length; b++) {
								if (elevatia[b] > max1) {
									max1 = elevatia[b];
								}
								if (elevatia[b] < min1) {
									min1 = elevatia[b];
								}
							}
							final double eleMax = max1;
							final double eleMi = min1;
							
							//System.out.println(eleMax + " " + eleMi + "\n");
							
							double max2 = distances[0];
							double min2 = distances[0];
							for (int b = 1; b < distances.length; b++) {
								if (distances[b] > max2) {
									max2 = distances[b];
								}
								if (distances[b] < min2) {
									min2 = distances[b];
								}
							}
							final double distMax = max2;
							final double distMin = min2;
							
							//System.out.println(distMax + " " + distMin + "\n");
							
							final int[] xValues = new int[distances.length];
							final int[] yValues = new int[distances.length];
							
							JPanel jp;
							boolean drawn = false;
							jp = new JPanel() {
								@Override
								protected void paintComponent(Graphics g) {
									//in prima parte desena linii pentru axele Ox și Oy, plus sirurile in care 
									//vom prezenta in metri distantele si altitudinile
									super.paintComponent(g);
									Graphics2D g2d = (Graphics2D) g;
									g2d.setStroke(new BasicStroke(2));
									int fontSize = 12;
									g2d.setFont(new Font("TimesRoman", Font.PLAIN, fontSize + 2));


									// Setam culoarea pentru axele x si y
									g2d.setColor(Color.BLACK);

									// Desenam axa x
									g2d.drawLine(50, 750, 1500, 750);
									
									int k = 20;
									int l = 0;
									
									g2d.setColor(Color.black);
									int	metres = (int) sumDist;
									for(int i = 0;i < metres; i += (metres / k)){
										String S = Integer.toString(i);
										g2d.drawString(S, 50 + (1500 / k) * l - 5 , 768);
										l++;
										if(l > k) break;
									}
									g2d.drawString("[m]", 1513, 752);

									// Desenam axa y
									g2d.drawLine(50, 10, 50, 750);
									
									int q = 20;
									int w = 0;
															
									g2d.setColor(Color.black);
									int	metres1 = (int) eleMax;
									for(int i = 0;i < metres1; i = i + (metres1 / q)){
										String S1 = Integer.toString(i);
										g2d.drawString(S1, 10, 750 - (750/ q) * w + 13);
										w++;
									}
									
									g2d.drawString("[m]", 53, 11);
									//puctulete pt elevatie cum am facut pt distanta
									
									// Setam culoarea pentru puncte si linii
									g2d.setColor(new Color(95,158,160));
									
									
									// Desenam punctele si legaturile dintre ele
									//calculez valoarea maxima si minima din fiecare vector (x si y de mai sus) xValues, yValues;
									//Daca valoarea maxima trece de 1500( lungimea maxima a ecranului meu)
									//Atunci trebuie sa scalez TOATE numerele
									//parcurg numerele si fiecare numar o sa devina ( xValues[i] = scaledNumber(xValues[i],1500,50,xMAX,xMIN) 
									//La fel si pentru y;
									//dupa scalare continui procesul de desenare de mai jos;
									//Parcurg xvalues( care o sa aiba aceeasi lungime cu yValues)
									//int x = xValues[i], y = yValues[i];
									//acuma tu lucrezi cu cei 2 vectori de mai sus nu cu vectorul distances.
										// Deseneaza punctul
									
									for (int i = 0; i < distances.length; i++) {
										// Calculeaza pozitia punctului pe grafic
										int x = 50 + (int)(i * (1500.0 / distances.length)) - 3;
										xValues[i] = x;
										//System.out.println(xValues[i]);
										
										int y = 750 - (int)(elevatia[i] * (750.0 / eleMax));
										yValues[i] = y;
										//Pun cei 2 valori in 2 vectori, unul pt x si unul pt y de mai sus
									}
									int xMAX = xValues[0];
									int xMIN = xValues[0];
									for(int i = 1;i < xValues.length; i++){
										if(xValues[i] > xMAX){
											xMAX = xValues[i];
										}
										if(xValues[i] < xMIN){
											xMIN = xValues[i];
										}
									}
									int yMAX = yValues[0];
									int yMIN = yValues[0];
									for(int i = 1;i < xValues.length; i++){
										if(yValues[i] > yMAX){
											yMAX = yValues[i];
										}
										if(yValues[i] < yMIN){
											yMIN = yValues[i];
										}
									}
									if(xMAX > 1500){
										for(int i = 0;i < xValues.length; i++){
											xValues[i] = scaledNumber(xValues[i],1500,50,xMAX,xMIN);
											//System.out.println(xValues[i]);
										}
									}
									if(yMAX > 800){
										for(int i = 0;i < yValues.length; i++){
											yValues[i] = scaledNumber(yValues[i],750,50,yMAX,yMIN);
											//System.out.println(yValues[i]);
										}
									}

									// Setăm culoarea pentru umplerea sub grafic
									g2d.setColor(new Color(147,112,219)); // am pus culoarea cadet blue

									// Creăm un poligon care să reprezinte suprafața sub grafic
									int[] xPoints = new int[xValues.length + 2];
									int[] yPoints = new int[yValues.length + 2];
									xPoints[0] = 50;
									yPoints[0] = 750;
									//System.out.println(xPoints[0]);
									xPoints[xValues.length + 1] = 50 + (int)(1300.0);
									yPoints[yValues.length + 1] = 750;
									//System.out.println(xPoints[xValues.length + 1]);
									for (int i = 0; i < xValues.length; i++) {
										xPoints[i+1] = 50 + (int)(i * (1300.0 / xValues.length));
										//System.out.println(xPoints[i]);
									}
									for (int i = 0; i < yValues.length; i++) {
										yPoints[i+1] = 750 - (int)(elevatia[i] * (750.0 / eleMax));
										//System.out.println(yPoints[i]);
									}
									Polygon polygon = new Polygon(xPoints, yPoints, xPoints.length);
									// Umplem poligonul cu culoarea setată mai sus
									g2d.fillPolygon(polygon);
									
									if (mousePressed) {
										g2d.setColor(Color.RED);
										int radius = 4;
										g2d.fillOval(mouseX - radius, mouseY - radius, 2 * radius, 2 * radius);
										g2d.drawLine(50, mouseY, mouseX, mouseY);
										g2d.drawLine(mouseX, 750, mouseX, mouseY);
									}
								}
								@Override
								public Dimension getPreferredSize() {
									return new Dimension(800, 600);
								}
							};	
							Dimension screenSize1 = Toolkit.getDefaultToolkit().getScreenSize();//Această linie de cod obține dimensiunea ecranului curent utilizând clasa Toolkit și metoda getScreenSize(). Rezultatul este stocat în variabila screenSize1 de tip Dimension, care va reprezenta dimensiunea ecranului
							
							JPanel casetaPanel = new JPanel();//Aici se creează un obiect de tip JPanel numit casetaPanel, care reprezintă caseta grafică în care se vor adăuga elementele
							casetaPanel.setBackground(new Color(186,85,211)); // Setează culoarea de fundal a casetei
							
							Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
							int lungimeLaptop = screenSize.width; // Obține lungimea ecranului
							casetaPanel.setPreferredSize(new Dimension(lungimeLaptop, 67)); // Setează dimensiunea casetei
							
							casetaPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 100, 10));
							JLabel caseta1 = new JLabel();
							caseta1.setHorizontalAlignment(SwingConstants.LEFT); // Aliniază textul pe stanga orizontal
							caseta1.setVerticalAlignment(SwingConstants.CENTER); // Aliniază textul pe centru vertical
							
							JLabel caseta2 = new JLabel();
							caseta2.setHorizontalAlignment(SwingConstants.LEFT); 
							caseta2.setVerticalAlignment(SwingConstants.CENTER); 
							
							JLabel caseta3 = new JLabel();
							caseta3.setHorizontalAlignment(SwingConstants.LEFT); 
							caseta3.setVerticalAlignment(SwingConstants.CENTER); 
							
							JLabel caseta4 = new JLabel();
							caseta4.setHorizontalAlignment(SwingConstants.LEFT); 
							caseta4.setVerticalAlignment(SwingConstants.CENTER); 
							
							JLabel caseta5 = new JLabel();
							caseta5.setHorizontalAlignment(SwingConstants.LEFT); 
							caseta5.setVerticalAlignment(SwingConstants.CENTER); 

							// casetele vor fi afișate în interiorul casetei grafice casetaPanel când aplicația este rulată
							casetaPanel.add(caseta1);
							casetaPanel.add(caseta2);
							casetaPanel.add(caseta3);
							casetaPanel.add(caseta4);
							casetaPanel.add(caseta5);
							
							caseta1.setBackground(new Color(255,192,203));
							caseta1.setOpaque(true); // Face caseta să fie vizibilă cu culoarea de fundal setată
							caseta1.setPreferredSize(new Dimension(150, 50)); // Setează dimensiunea casetei
							
							caseta2.setBackground(new Color(219,112,147));
							caseta2.setOpaque(true); 
							caseta2.setPreferredSize(new Dimension(150, 50)); 
							
							caseta3.setBackground(new Color(218,112,214));
							caseta3.setOpaque(true); 
							caseta3.setPreferredSize(new Dimension(150, 50)); 
							
							caseta4.setBackground(new Color(218,112,214));
							caseta4.setOpaque(true); 
							caseta4.setPreferredSize(new Dimension(225, 50));
							
							caseta5.setBackground(new Color(218,112,214));
							caseta5.setOpaque(true); 
							caseta5.setPreferredSize(new Dimension(225, 50)); 
							
							//recalculează și reafișează corect dimensiunile și pozițiile componentelor sale în funcție de eventuale schimbări sau adăugiri de componente
							casetaPanel.revalidate();
							//sigură redesenarea casetei pentru a reflecta aceste modificări
							casetaPanel.repaint();
							
							jp.addMouseListener(new MouseAdapter() {
								int distantaParcursa;
								@Override
									public void mousePressed(MouseEvent e) {
										mouseX = e.getX();
										mouseY = e.getY();
										mousePressed = true;
										jp.repaint();
										
										double latitudinea = calculateLatitudine(mouseX); // Calculați distanța parcursă în funcție de mouseX
										caseta1.setText("Latitudinea: " + latitudinea);

										casetaPanel.revalidate();
										casetaPanel.repaint();
										
										double longitudinea = calculateLongitudine(mouseX); // Calculați distanța parcursă în funcție de mouseX
										caseta2.setText("Longitudine: " + longitudinea);

										casetaPanel.revalidate();
										casetaPanel.repaint();
										
										double elevatia = calculateElevation(mouseY); // Calculați distanța parcursă în funcție de mouseX
										caseta3.setText("Elevatie: " + elevatia);

										casetaPanel.revalidate();
										casetaPanel.repaint();
										
										distantaParcursa = calculateDistance(0, mouseX); // Calculați distanța parcursă în funcție de mouseX
										caseta4.setText("Distanta parcursa: " + distantaParcursa);

										casetaPanel.revalidate();
										casetaPanel.repaint();
										
										int distantaRamasa = calculateDistanceToFinalDistance(mouseX, mouseY); // Calculați distanța parcursă în funcție de mouseX
										caseta5.setText("Distanta ramasa: " + distantaRamasa);

										casetaPanel.revalidate();
										casetaPanel.repaint();
									}							
								@Override
								public void mouseClicked(MouseEvent e) {
									int distantaParcursa1 = calculateDistance(0, mouseX); 
									distantaParcursa1 /= 1000;
									System.out.println(distantaParcursa1 + "\n");
									double panta = calculateSlope(0, e.getX(), e.getY());
									double ascedent = calculareAscedent(e.getY());
									double descedent = calculareDescedent(e.getY());
									double calorii1 = calculateCalorii(gender, age, kilograme1, inaltime1, backpack, distantaParcursa1, panta, 9000);
									super.mouseClicked(e);
									if (e.getButton() == MouseEvent.BUTTON1) { // Verificăm dacă clicul a fost cu butonul stânga al mouse-ului
										if (dialog != null) {
											dialog.dispose();
										}

										dialog = new MyNewDialog();
										int screenWidth = screenSize1.width;
										int screenHeight = screenSize1.height;
										
										int newX = screenWidth - dialog.getWidth() - 30; 
										int newY = 100; 
										
										dialog.setLocation(newX, newY); 

										dialog.setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.Y_AXIS)); // Setează layout-ul vertical
										
										// Creează etichete pentru fiecare text și adaugă-le în fereastră
										JLabel label1 = new JLabel("Panta: " );
										JLabel label2 = new JLabel("Ascedent: " );
										JLabel label3 = new JLabel("Descedent: " );
										JLabel label4 = new JLabel("Gender: " );
										JLabel label5 = new JLabel("Varsta: " );
										JLabel label6 = new JLabel("Kilograme: " );
										JLabel label7 = new JLabel("Inaltime: " );
										JLabel label8 = new JLabel("Greutatea ghiozdanului: " );
										JLabel label9 = new JLabel("Calorii: " );
										
										JLabel valueLabel1 = new JLabel(String.valueOf(panta));
										JLabel valueLabel2 = new JLabel(String.valueOf(ascedent));
										JLabel valueLabel3 = new JLabel(String.valueOf(descedent));
										JLabel valueLabel4 = new JLabel(String.valueOf(gender));
										JLabel valueLabel5 = new JLabel(String.valueOf(age));
										JLabel valueLabel6 = new JLabel(String.valueOf(kilograme1));
										JLabel valueLabel7 = new JLabel(String.valueOf(inaltime1));
										JLabel valueLabel8 = new JLabel(String.valueOf(backpack));
										JLabel valueLabel9 = new JLabel(String.valueOf(calorii1));
										
										int verticalSpacing = 25; // Spațiu vertical între etichetele de text

										// Setează alinierea la centru pentru fiecare etichetă
										label1.setHorizontalAlignment(SwingConstants.CENTER);
										label2.setHorizontalAlignment(SwingConstants.CENTER);
										label3.setHorizontalAlignment(SwingConstants.CENTER);
										label4.setHorizontalAlignment(SwingConstants.CENTER);
										label5.setHorizontalAlignment(SwingConstants.CENTER);
										label6.setHorizontalAlignment(SwingConstants.CENTER);
										label7.setHorizontalAlignment(SwingConstants.CENTER);
										label8.setHorizontalAlignment(SwingConstants.CENTER);
										label9.setHorizontalAlignment(SwingConstants.CENTER);

										// Adaugă spațiu vertical între etichete
										dialog.add(Box.createVerticalStrut(verticalSpacing));
										valueLabel1.setHorizontalAlignment(SwingConstants.RIGHT);
										valueLabel2.setHorizontalAlignment(SwingConstants.RIGHT);
										valueLabel3.setHorizontalAlignment(SwingConstants.RIGHT);
										valueLabel4.setHorizontalAlignment(SwingConstants.RIGHT);
										valueLabel5.setHorizontalAlignment(SwingConstants.RIGHT);
										valueLabel6.setHorizontalAlignment(SwingConstants.RIGHT);
										valueLabel7.setHorizontalAlignment(SwingConstants.RIGHT);
										valueLabel8.setHorizontalAlignment(SwingConstants.RIGHT);
										valueLabel9.setHorizontalAlignment(SwingConstants.RIGHT);
										
										dialog.add(label1);
										dialog.add(valueLabel1);
										dialog.add(Box.createVerticalStrut(verticalSpacing));
										dialog.add(label2);
										dialog.add(valueLabel2);
										dialog.add(Box.createVerticalStrut(verticalSpacing));
										dialog.add(label3);
										dialog.add(valueLabel3);
										dialog.add(Box.createVerticalStrut(verticalSpacing));
										dialog.add(label4);
										dialog.add(valueLabel4);
										dialog.add(Box.createVerticalStrut(verticalSpacing));
										dialog.add(label5);
										dialog.add(valueLabel5);
										dialog.add(Box.createVerticalStrut(verticalSpacing));
										dialog.add(label6);
										dialog.add(valueLabel6);
										dialog.add(Box.createVerticalStrut(verticalSpacing));
										dialog.add(label7);
										dialog.add(valueLabel7);
										dialog.add(Box.createVerticalStrut(verticalSpacing));
										dialog.add(label8);
										dialog.add(valueLabel8);
										dialog.add(Box.createVerticalStrut(verticalSpacing));
										dialog.add(label9);
										dialog.add(valueLabel9);
										dialog.add(Box.createVerticalStrut(verticalSpacing));
										
										dialog.setVisible(true);	
									}
								}
								
								// Metoda pentru a calcula latitudinea corespunzătoare punctului de pe grafic unde s-a făcut click
								private double calculateLatitudine(double mouseX) {
									int index = -1;
									int numPoints = xValues.length; // lungimea vectorului xValues

									// Parcurgem vectorul xValues și căutăm cel mai apropiat punct de pe grafic
									for (int i = 0; i < numPoints; i++) {
										if (mouseX >= xValues[i] - 15&& mouseX <= xValues[i] + 15) {
											index = i;
											break;
										}
									}
									// Returnăm indexul găsit sau -1 în cazul în care nu s-a găsit niciun punct apropiat
									return latitudine[index];
								}
								
								// Metoda pentru a calcula longitudine corespunzătoare punctului de pe grafic unde s-a făcut click
								private double calculateLongitudine(double mouseX) {
									int index = -1;
									int numPoints = xValues.length; // lungimea vectorului xValues

									// Parcurgem vectorul xValues și căutăm cel mai apropiat punct de pe grafic
									for (int i = 0; i < numPoints; i++) {
										if (mouseX >= xValues[i] - 20 && mouseX <= xValues[i] + 20) {
											index = i;
											break;
										}
									}
									// Returnăm indexul găsit sau -1 în cazul în care nu s-a găsit niciun punct apropiat
									return longitudine[index];
								}
								
								private double calculateElevation(int clickY) {
									double targetElevation = eleMax - ((clickY - 10) * (eleMax / 750.0));
									// Cautăm valoarea de elevație cea mai apropiată și returnăm-o
									double elevation = Double.MAX_VALUE;
									double minDifference = Double.MAX_VALUE;
									for (int i = 0; i < elevatia.length; i++) {
										double difference = Math.abs(elevatia[i] - targetElevation);
										if (difference < minDifference) {
											minDifference = difference;
											elevation = elevatia[i];
										}
									}

									// Rotunjim valoarea de elevație la 2 zecimale
									elevation = Math.round(elevation * 100.0) / 100.0;
									return elevation;
								}
								
								private int calculateDistance(int startX, int endX) {
									int index = -1;
									int numPoints = xValues.length; // lungimea vectorului xValues
									int totalDistance = 0;

									// Parcurgem vectorul xValues și căutăm cel mai apropiat punct de pe grafic
									for (int i = 0; i < numPoints; i++) {
										if (mouseX >= xValues[i] - 20 && mouseX <= xValues[i] + 20) {
											index = i;
											break;
										}
									}
									for(int i = 0;i < index; i++){
										//System.out.println(xValues + " ");
										totalDistance += distances[i];
										//System.out.println(totalDistance + "\n");
									}
									return totalDistance;
								}
								
								private int calculateDistanceToFinalDistance(int x, int y) {
									int distantaParcursa = calculateDistance(0, x); // Calculează distanța de la început până la punctul x, y
									int sumDist1 = (int)sumDist;
									// Calculează distanța rămasă până la distanța finală
									int remainingDistance = sumDist1 - distantaParcursa;
									
									return remainingDistance;
								}
								
								private double calculateSlope(int startX,int endX, int clickY) {
									double targetElevation = eleMax - ((clickY - 10) * (eleMax / 750.0));
									// Cautăm valoarea de elevație cea mai apropiată și returnăm-o
									double elevation = Double.MAX_VALUE;
									double minDifference = Double.MAX_VALUE;
									int index = -1;
									for (int i = 0; i < elevatia.length; i++) {
										double difference = Math.abs(elevatia[i] - targetElevation);
										if (difference < minDifference) {
											minDifference = difference;
											elevation = elevatia[i];
											index = i;
										}
									}
									double distance = 0.0;
									double slope = 0.0;
									double finalSlope = 0.0;
									for(int i = 2;i < index - 2; i++){
										distance = calculateDistance(startX, endX);
										if(elevatia[i] > elevatia[i+1] && elevatia[i+1] <= elevatia[i+2]){
											distance = calculateDistance(startX, endX);
											slope = elevatia[i+1] / distance;
											finalSlope -= slope;
										}
										else if(elevatia[i] < elevatia[i+1] && elevatia[i+1] >= elevatia[i+2]){
											distance = calculateDistance(startX, endX);
											slope = elevatia[i+1] / distance;
											finalSlope += slope;
										}
									}
									return finalSlope;
								}
								private double calculareAscedent(int clickY){
									double ascedent = 0.0;
									double targetElevation = eleMax - ((clickY - 10) * (eleMax / 750.0));
									// Cautăm valoarea de elevație cea mai apropiată și returnăm-o
									double elevation = Double.MAX_VALUE;
									double minDifference = Double.MAX_VALUE;
									int index = -1;
									for (int i = 0; i < elevatia.length; i++) {
										double difference = Math.abs(elevatia[i] - targetElevation);
										if (difference < minDifference) {
											minDifference = difference;
											elevation = elevatia[i];
											index = i;
										}
									}
									for(int i = 2; i < index - 1; i++){
										if(elevatia[i] < elevatia[i+1]){
											ascedent += (elevatia[i+1] - elevatia[i]);
										}
									}
									return ascedent;
								}
								
								private double calculareDescedent(int clickY){
									double descedent = 0.0;
									double targetElevation = eleMax - ((clickY - 10) * (eleMax / 750.0));
									// Cautăm valoarea de elevație cea mai apropiată și returnăm-o
									double elevation = Double.MAX_VALUE;
									double minDifference = Double.MAX_VALUE;
									int index = -1;
									for (int i = 0; i < elevatia.length; i++) {
										double difference = Math.abs(elevatia[i] - targetElevation);
										if (difference < minDifference) {
											minDifference = difference;
											elevation = elevatia[i];
											index = i;
										}
									}
									for(int i = 2; i < index - 1; i++){
										if(elevatia[i] > elevatia[i+1]){
											descedent += (elevatia[i] - elevatia[i+1]);
										}
									}
									return descedent;
								}
								private double calculateCalorii(String gender, double age, double weight, double height, double backpack_weight, double distance, double avg_slope_procent, double moving_time_seconds){
									double calories = calorieCalculator.totalCalories(gender,age, weight, height, backpack_weight, distance, avg_slope_procent, moving_time_seconds);
									return calories;
								}
							});
							
							jp.setBackground(new Color(230,230,250)); // set the background color of the jp panel
							
							// Creează un obiect JFrame
							JFrame frame = new JFrame("Track Analyser");
							frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
							frame.getContentPane().setBackground( Color.blue );
							frame.setResizable(false);
							
							// Adauga panoul care afiseaza graficul la frame
							frame.add(jp, BorderLayout.CENTER);
							frame.add(casetaPanel, BorderLayout.SOUTH); // Adaugă casetaPanel la partea de jos a frame-ului

							frame.setLocation(0, 0);
							frame.setVisible(true);				
							
							br.close();
						 }
						catch (Exception ex) {
								ex.printStackTrace();
						}
					 }
					else if (fileExtension.equals("kml")) {
						// Fișierul este KML
						try {
							BufferedReader br = new BufferedReader(new FileReader(filepath));
							String LiniaDeCoordonate = "";//Se declara și inițializează variabila LiniaDeCoordonate ca un șir vid. Aceasta va fi utilizată pentru a stoca linia de coordonate găsită în fișier

							String line;
							boolean found = false;
							//Se parcurge fiecare linie a fișierului într-un buclă while și se caută linia care conține "<coordinates>". Când linia este găsită, se setează found pe true.
							while ((line = br.readLine()) != null) {
								if (line.contains("<coordinates>")) {
									found = true;
									continue;
								}
								if (line.contains("</coordinates>")) {
									break;
								}

								if (found) {
									LiniaDeCoordonate = line.trim();
									break;
								}
							}
							//Se elimină spațiile goale de la începutul și sfârșitul șirului LiniaDeCoordonate și se împarte acesta într-un array de șiruri coordonate utilizând spațiul ca separator.
							String[] coordonate = LiniaDeCoordonate.trim().split(" ");
							int lungimea = coordonate.length;
							//Se declară și se inițializează trei array-uri de numere reale: longitudine, latitudine și elevatia, fiecare având lungimea lungimea
							double[] longitudine = new double[lungimea];
							double[] latitudine = new double[lungimea];
							double[] elevatia = new double[lungimea];
							//Se parcurge fiecare element din array-ul coordonate, se separă valorile latitudinii, longitudinii și altitudinii utilizând virgula ca separator și se stochează în array-urile corespunzătoare//
							for (int k = 0; k < lungimea; k++) {
								String[] temp = coordonate[k].split(",");
								longitudine[k] = Double.parseDouble(temp[0]);
								latitudine[k] = Double.parseDouble(temp[1]);
								elevatia[k] = Double.parseDouble(temp[2]);
							}
							
							for(int j = 0;j < lungimea;j++){		
								int lat = (int)latitudine[j];
								int lon = (int)longitudine[j];
								double maxlat = lat+1;
								double maxlon = lon+1;		
								if(maxlat<=latitudine[j]) lat++;
								if(maxlon<=longitudine[j]) lon++;								
								String name = "n0"+lat+"e0"+lon;	
								if(dem==null) dem = new DEM(name);
								else if(!dem.name.startsWith(name)){
									dem.close();
									dem = new DEM(name);
								}
								elevatia[j] = dem.getElevation(longitudine[j], latitudine[j]);
							}

							for(int k = 0;k < lungimea;k++){
								ta.append("lat : " + latitudine[k] + " ");
								ta.append("lon : " + longitudine[k] + " ");
								ta.append("elevatie1 : " + elevatia[k] + " ");
								ta.append("\n");
							}
							double[] distances = new double[latitudine.length - 1];
							
							for (int a = 2; a < latitudine.length - 1; a++) {
								double dLat = Math.toRadians(latitudine[a+1] - latitudine[a]);
								double dLon = Math.toRadians(longitudine[a+1] - longitudine[a]);
						 
								final int R = 6371; // Radius of the earth
								double har = Math.sin(dLat / 2) * Math.sin(dLat / 2)
										+ Math.cos(Math.toRadians(latitudine[a])) * Math.cos(Math.toRadians(latitudine[a+1]))
										* Math.sin(dLon / 2) * Math.sin(dLon / 2);
								double c = 2 * Math.atan2(Math.sqrt(har), Math.sqrt(1 - har));
								distances[a] = R * c * 1000;
							}
							
							double sum = 0;
							for (int a = 2; a < latitudine.length - 1; a++) {
								sum += distances[a];
							}
							final double sumDist = sum;
							
							double max1 = elevatia[0];
							double min1 = elevatia[0];
							for (int b = 1; b < elevatia.length; b++) {
								if (elevatia[b] > max1) {
									max1 = elevatia[b];
								}
								if (elevatia[b] < min1) {
									min1 = elevatia[b];
								}
							}
							final double eleMax = max1;
							final double eleMi = min1;
							
							double max2 = distances[0];
							double min2 = distances[0];
							for (int b = 1; b < distances.length; b++) {
								if (distances[b] > max2) {
									max2 = distances[b];
								}
								if (distances[b] < min2) {
									min2 = distances[b];
								}
							}
							final double distMax = max2;
							final double distMin = min2;
							
							final int[] xValues = new int[distances.length];
							final int[] yValues = new int[distances.length];
							
							JPanel jp;
							boolean drawn = false;
							jp = new JPanel() {
								@Override
								protected void paintComponent(Graphics g) {
									super.paintComponent(g);
									Graphics2D g2d = (Graphics2D) g;
									g2d.setStroke(new BasicStroke(2));
									int fontSize = 12;
									g2d.setFont(new Font("TimesRoman", Font.PLAIN, fontSize + 2));


									// Setam culoarea pentru axele x si y
									g2d.setColor(Color.BLACK);

									// Desenam axa x
									g2d.drawLine(50, 750, 1500, 750);
									
									int k = 30;
									int l = 0;
									
									g2d.setColor(Color.black);
									int	metres = (int) sumDist;
									for(int i = 0;i < metres; i += (metres / k)){
										String S = Integer.toString(i);
										g2d.drawString(S, 50 + (1500 / k) * l - 5 , 768);
										l++;
										if(l > k) break;
									}
									g2d.drawString("[km]", 1513, 752);

									// Desenam axa y
									g2d.drawLine(50, 10, 50, 750);
									
									int q = 20;
									int w = 0;
															
									g2d.setColor(Color.black);
									int	metres1 = (int) eleMax;
									for(int i = 0;i < metres1; i = i + (metres1 / q)){
										String S1 = Integer.toString(i);
										g2d.drawString(S1, 10, 750 - (750/ q) * w + 13);
										w++;
									}
									
									g2d.drawString("[m]", 53, 11);
									//puctulete pt elevatie cum am facut pt distanta
									
									// Setam culoarea pentru puncte si linii
									g2d.setColor(new Color(95,158,160));
									
									
									// Desenam punctele si legaturile dintre ele
									//calculez valoarea maxima si minima din fiecare vector (x si y de mai sus) xValues, yValues;
									//Daca valoarea maxima trece de 1500( lungimea maxima a ecranului meu)
									//Atunci trebuie sa scalez TOATE numerele
									//parcurg numerele si fiecare numar o sa devina ( xValues[i] = scaledNumber(xValues[i],1500,50,xMAX,xMIN) 
									//La fel si pentru y;
									//dupa scalare continui procesul de desenare de mai jos;
									//Parcurg xvalues( care o sa aiba aceeasi lungime cu yValues)
									//int x = xValues[i], y = yValues[i];
									//acuma tu lucrezi cu cei 2 vectori de mai sus nu cu vectorul distances.
										// Deseneaza punctul
									
									for (int i = 0; i < distances.length; i++) {
										// Calculeaza pozitia punctului pe grafic
										int x = 50 + (int)(i * (1500.0 / distances.length)) - 3;
										xValues[i] = x;
										
										int y = 750 - (int)(elevatia[i] * (750.0 / eleMax));
										yValues[i] = y;
										//Pun cei 2 valori in 2 vectori, unul pt x si unul pt y de mai sus
									}
									int xMAX = xValues[0];
									int xMIN = xValues[0];
									for(int i = 1;i < xValues.length; i++){
										if(xValues[i] > xMAX){
											xMAX = xValues[i];
										}
										if(xValues[i] < xMIN){
											xMIN = xValues[i];
										}
									}
									int yMAX = yValues[0];
									int yMIN = yValues[0];
									for(int i = 1;i < xValues.length; i++){
										if(yValues[i] > yMAX){
											yMAX = yValues[i];
										}
										if(yValues[i] < yMIN){
											yMIN = yValues[i];
										}
									}
									if(xMAX > 1500){
										for(int i = 0;i < xValues.length; i++){
											xValues[i] = scaledNumber(xValues[i],1500,50,xMAX,xMIN);
											//System.out.println(xValues[i]);
										}
									}
									if(yMAX > 800){
										for(int i = 0;i < yValues.length; i++){
											yValues[i] = scaledNumber(yValues[i],750,50,yMAX,yMIN);
											//System.out.println(yValues[i]);
										}
									}

									// Setăm culoarea pentru umplerea sub grafic
									g2d.setColor(new Color(147,112,219)); // am pus culoarea cadet blue

									// Creăm un poligon care să reprezinte suprafața sub grafic
									int[] xPoints = new int[xValues.length + 2];
									int[] yPoints = new int[yValues.length + 2];
									xPoints[0] = 50;
									yPoints[0] = 750;
									//System.out.println(xPoints[0]);
									xPoints[xValues.length + 1] = 50 + (int)(1300.0);
									yPoints[yValues.length + 1] = 750;
									//System.out.println(xPoints[xValues.length + 1]);
									for (int i = 0; i < xValues.length; i++) {
										xPoints[i+1] = 50 + (int)(i * (1300.0 / xValues.length));
										//System.out.println(xPoints[i]);
									}
									for (int i = 0; i < yValues.length; i++) {
										yPoints[i+1] = 750 - (int)(elevatia[i] * (750.0 / eleMax));
										//System.out.println(yPoints[i]);
									}
									Polygon polygon = new Polygon(xPoints, yPoints, xPoints.length);
									// Umplem poligonul cu culoarea setată mai sus
									g2d.fillPolygon(polygon);
									
									if (mousePressed) {
										g2d.setColor(Color.RED);
										int radius = 4;
										g2d.fillOval(mouseX - radius, mouseY - radius, 2 * radius, 2 * radius);
										g2d.drawLine(50, mouseY, mouseX, mouseY);
										g2d.drawLine(mouseX, 750, mouseX, mouseY);
									}
								}
								@Override
								public Dimension getPreferredSize() {
									return new Dimension(800, 600);
								}
							};	
							Dimension screenSize1 = Toolkit.getDefaultToolkit().getScreenSize();
							
							JPanel casetaPanel = new JPanel();
							casetaPanel.setBackground(new Color(186,85,211)); // Setează culoarea de fundal a casetei
							
							Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
							int lungimeLaptop = screenSize.width; // Obține lungimea ecranului
							casetaPanel.setPreferredSize(new Dimension(lungimeLaptop, 67)); // Setează dimensiunea casetei
							
							casetaPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 100, 10));
							JLabel caseta1 = new JLabel();
							caseta1.setHorizontalAlignment(SwingConstants.LEFT); // Aliniază textul pe stanga orizontal
							caseta1.setVerticalAlignment(SwingConstants.CENTER); // Aliniază textul pe centru vertical
							
							JLabel caseta2 = new JLabel();
							caseta2.setHorizontalAlignment(SwingConstants.LEFT); // Aliniază textul pe stanga orizontal
							caseta2.setVerticalAlignment(SwingConstants.CENTER); // Aliniază textul pe centru vertical
							
							JLabel caseta3 = new JLabel();
							caseta3.setHorizontalAlignment(SwingConstants.LEFT); // Aliniază textul pe stanga orizontal
							caseta3.setVerticalAlignment(SwingConstants.CENTER); // Aliniază textul pe centru vertical
							
							JLabel caseta4 = new JLabel();
							caseta4.setHorizontalAlignment(SwingConstants.LEFT); // Aliniază textul pe stanga orizontal
							caseta4.setVerticalAlignment(SwingConstants.CENTER); // Aliniază textul pe centru vertical
							
							JLabel caseta5 = new JLabel();
							caseta5.setHorizontalAlignment(SwingConstants.LEFT); // Aliniază textul pe stanga orizontal
							caseta5.setVerticalAlignment(SwingConstants.CENTER); // Aliniază textul pe centru vertical

							casetaPanel.add(caseta1);
							casetaPanel.add(caseta2);
							casetaPanel.add(caseta3);
							casetaPanel.add(caseta4);
							casetaPanel.add(caseta5);
							
							caseta1.setBackground(new Color(255,192,203));
							caseta1.setOpaque(true); // Face caseta să fie vizibilă cu culoarea de fundal setată
							caseta1.setPreferredSize(new Dimension(150, 50)); // Setează dimensiunea casetei
							
							caseta2.setBackground(new Color(219,112,147));
							caseta2.setOpaque(true); // Face caseta să fie vizibilă cu culoarea de fundal setată
							caseta2.setPreferredSize(new Dimension(150, 50)); // Setează dimensiunea casetei
							
							caseta3.setBackground(new Color(218,112,214));
							caseta3.setOpaque(true); // Face caseta să fie vizibilă cu culoarea de fundal setată
							caseta3.setPreferredSize(new Dimension(150, 50)); // Setează dimensiunea casetei
							
							caseta4.setBackground(new Color(218,112,214));
							caseta4.setOpaque(true); // Face caseta să fie vizibilă cu culoarea de fundal setată
							caseta4.setPreferredSize(new Dimension(225, 50)); // Setează dimensiunea casetei
							
							caseta5.setBackground(new Color(218,112,214));
							caseta5.setOpaque(true); // Face caseta să fie vizibilă cu culoarea de fundal setată
							caseta5.setPreferredSize(new Dimension(225, 50)); // Setează dimensiunea casetei
							
							casetaPanel.revalidate();
							casetaPanel.repaint();
							
							jp.addMouseListener(new MouseAdapter() {
								int distantaParcursa;
								@Override
									public void mousePressed(MouseEvent e) {
										mouseX = e.getX();
										mouseY = e.getY();
										mousePressed = true;
										jp.repaint();
										
										double latitudinea = calculateLatitudine(mouseX); // Calculați distanța parcursă în funcție de mouseX
										caseta1.setText("Latitudinea: " + latitudinea);

										casetaPanel.revalidate();
										casetaPanel.repaint();
										
										double longitudinea = calculateLongitudine(mouseX); // Calculați distanța parcursă în funcție de mouseX
										caseta2.setText("Longitudine: " + longitudinea);

										casetaPanel.revalidate();
										casetaPanel.repaint();
										
										double elevatia = calculateElevation(mouseY); // Calculați distanța parcursă în funcție de mouseX
										caseta3.setText("Elevatie: " + elevatia);

										casetaPanel.revalidate();
										casetaPanel.repaint();
										
										distantaParcursa = calculateDistance(0, mouseX); // Calculați distanța parcursă în funcție de mouseX
										caseta4.setText("Distanta parcursa: " + distantaParcursa);

										casetaPanel.revalidate();
										casetaPanel.repaint();
										
										int distantaRamasa = calculateDistanceToFinalDistance(mouseX, mouseY); // Calculați distanța parcursă în funcție de mouseX
										caseta5.setText("Distanta ramasa: " + distantaRamasa);

										casetaPanel.revalidate();
										casetaPanel.repaint();
									}							
								@Override
								public void mouseClicked(MouseEvent e) {
									int distantaParcursa1 = calculateDistance(0, mouseX); 
									distantaParcursa1 /= 1000;
									System.out.println(distantaParcursa1 + "\n");
									double panta = calculateSlope(0, e.getX(), e.getY());
									double ascedent = calculareAscedent(e.getY());
									double descedent = calculareDescedent(e.getY());
									double calorii1 = calculateCalorii(gender, age, kilograme1, inaltime1, backpack, distantaParcursa1, panta, 9000);
									super.mouseClicked(e);
									if (e.getButton() == MouseEvent.BUTTON1) { // Verificăm dacă clicul a fost cu butonul stânga al mouse-ului
										if (dialog != null) {
											dialog.dispose();
										}

										dialog = new MyNewDialog();
										int screenWidth = screenSize1.width;
										int screenHeight = screenSize1.height;
										
										int newX = screenWidth - dialog.getWidth() - 30; // Exemplu: decalaj de 100 pixeli de la marginea dreaptă a ecranului
										int newY = 100; // Exemplu: decalaj de 100 pixeli de la marginea de sus a ecranului
										
										dialog.setLocation(newX, newY); // Setăm noua poziție a ferestrei
										
										dialog.setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.Y_AXIS)); // Setează layout-ul vertical
										
										// Creează etichete pentru fiecare text și adaugă-le în fereastră
										JLabel label1 = new JLabel("Panta: " );
										JLabel label2 = new JLabel("Ascedent: " );
										JLabel label3 = new JLabel("Descedent: " );
										JLabel label4 = new JLabel("Gender: " );
										JLabel label5 = new JLabel("Varsta: " );
										JLabel label6 = new JLabel("Kilograme: " );
										JLabel label7 = new JLabel("Inaltime: " );
										JLabel label8 = new JLabel("Greutatea ghiozdanului: " );
										JLabel label9 = new JLabel("Calorii: " );
										
										JLabel valueLabel1 = new JLabel(String.valueOf(panta));
										JLabel valueLabel2 = new JLabel(String.valueOf(ascedent));
										JLabel valueLabel3 = new JLabel(String.valueOf(descedent));
										JLabel valueLabel4 = new JLabel(String.valueOf(gender));
										JLabel valueLabel5 = new JLabel(String.valueOf(age));
										JLabel valueLabel6 = new JLabel(String.valueOf(kilograme1));
										JLabel valueLabel7 = new JLabel(String.valueOf(inaltime1));
										JLabel valueLabel8 = new JLabel(String.valueOf(backpack));
										JLabel valueLabel9 = new JLabel(String.valueOf(calorii1));
										
										int verticalSpacing = 25; // Spațiu vertical între etichetele de text

										// Setează alinierea la centru pentru fiecare etichetă
										label1.setHorizontalAlignment(SwingConstants.CENTER);
										label2.setHorizontalAlignment(SwingConstants.CENTER);
										label3.setHorizontalAlignment(SwingConstants.CENTER);
										label4.setHorizontalAlignment(SwingConstants.CENTER);
										label5.setHorizontalAlignment(SwingConstants.CENTER);
										label6.setHorizontalAlignment(SwingConstants.CENTER);
										label7.setHorizontalAlignment(SwingConstants.CENTER);
										label8.setHorizontalAlignment(SwingConstants.CENTER);
										label9.setHorizontalAlignment(SwingConstants.CENTER);

										// Adaugă spațiu vertical între etichete
										dialog.add(Box.createVerticalStrut(verticalSpacing));
										valueLabel1.setHorizontalAlignment(SwingConstants.RIGHT);
										valueLabel2.setHorizontalAlignment(SwingConstants.RIGHT);
										valueLabel3.setHorizontalAlignment(SwingConstants.RIGHT);
										valueLabel4.setHorizontalAlignment(SwingConstants.RIGHT);
										valueLabel5.setHorizontalAlignment(SwingConstants.RIGHT);
										valueLabel6.setHorizontalAlignment(SwingConstants.RIGHT);
										valueLabel7.setHorizontalAlignment(SwingConstants.RIGHT);
										valueLabel8.setHorizontalAlignment(SwingConstants.RIGHT);
										valueLabel9.setHorizontalAlignment(SwingConstants.RIGHT);
										
										dialog.add(label1);
										dialog.add(valueLabel1);
										dialog.add(Box.createVerticalStrut(verticalSpacing));
										dialog.add(label2);
										dialog.add(valueLabel2);
										dialog.add(Box.createVerticalStrut(verticalSpacing));
										dialog.add(label3);
										dialog.add(valueLabel3);
										dialog.add(Box.createVerticalStrut(verticalSpacing));
										dialog.add(label4);
										dialog.add(valueLabel4);
										dialog.add(Box.createVerticalStrut(verticalSpacing));
										dialog.add(label5);
										dialog.add(valueLabel5);
										dialog.add(Box.createVerticalStrut(verticalSpacing));
										dialog.add(label6);
										dialog.add(valueLabel6);
										dialog.add(Box.createVerticalStrut(verticalSpacing));
										dialog.add(label7);
										dialog.add(valueLabel7);
										dialog.add(Box.createVerticalStrut(verticalSpacing));
										dialog.add(label8);
										dialog.add(valueLabel8);
										dialog.add(Box.createVerticalStrut(verticalSpacing));
										dialog.add(label9);
										dialog.add(valueLabel9);
										dialog.add(Box.createVerticalStrut(verticalSpacing));
										
										dialog.setVisible(true);	
									}
								}
								
								// Metoda pentru a calcula latitudinea corespunzătoare punctului de pe grafic unde s-a făcut click
								private double calculateLatitudine(double mouseX) {
									int index = -1;
									int numPoints = xValues.length; // lungimea vectorului xValues

									// Parcurgem vectorul xValues și căutăm cel mai apropiat punct de pe grafic
									for (int i = 0; i < numPoints; i++) {
										if (mouseX >= xValues[i] - 15&& mouseX <= xValues[i] + 15) {
											index = i;
											break;
										}
									}
									// Returnăm indexul găsit sau -1 în cazul în care nu s-a găsit niciun punct apropiat
									return latitudine[index];
								}
								
								// Metoda pentru a calcula longitudine corespunzătoare punctului de pe grafic unde s-a făcut click
								private double calculateLongitudine(double mouseX) {
									int index = -1;
									int numPoints = xValues.length; // lungimea vectorului xValues

									// Parcurgem vectorul xValues și căutăm cel mai apropiat punct de pe grafic
									for (int i = 0; i < numPoints; i++) {
										if (mouseX >= xValues[i] - 20 && mouseX <= xValues[i] + 20) {
											index = i;
											break;
										}
									}
									// Returnăm indexul găsit sau -1 în cazul în care nu s-a găsit niciun punct apropiat
									return longitudine[index];
								}
								
								private double calculateElevation(int clickY) {
									double targetElevation = eleMax - ((clickY - 10) * (eleMax / 750.0));
									// Cautăm valoarea de elevație cea mai apropiată și returnăm-o
									double elevation = Double.MAX_VALUE;
									double minDifference = Double.MAX_VALUE;
									for (int i = 0; i < elevatia.length; i++) {
										double difference = Math.abs(elevatia[i] - targetElevation);
										if (difference < minDifference) {
											minDifference = difference;
											elevation = elevatia[i];
										}
									}

									// Rotunjim valoarea de elevație la 2 zecimale
									elevation = Math.round(elevation * 100.0) / 100.0;
									return elevation;
								}
								
								private int calculateDistance(int startX, int endX) {
									int index = -1;
									int numPoints = xValues.length; // lungimea vectorului xValues
									int totalDistance = 0;

									// Parcurgem vectorul xValues și căutăm cel mai apropiat punct de pe grafic
									for (int i = 0; i < numPoints; i++) {
										if (mouseX >= xValues[i] - 20 && mouseX <= xValues[i] + 20) {
											index = i;
											break;
										}
									}
									for(int i = 0;i < index; i++){
										//System.out.println(xValues + " ");
										totalDistance += distances[i];
										//System.out.println(totalDistance + "\n");
									}
									return totalDistance;
								}
								
								private int calculateDistanceToFinalDistance(int x, int y) {
									int distantaParcursa = calculateDistance(0, x); // Calculează distanța de la început până la punctul x, y
									int sumDist1 = (int) sumDist;
									// Calculează distanța rămasă până la distanța finală
									int remainingDistance = sumDist1 - distantaParcursa;
									
									return remainingDistance;
								}
								
								private double calculateSlope(int startX,int endX, int clickY) {
									double targetElevation = eleMax - ((clickY - 10) * (eleMax / 750.0));
									// Cautăm valoarea de elevație cea mai apropiată și returnăm-o
									double elevation = Double.MAX_VALUE;
									double minDifference = Double.MAX_VALUE;
									int index = -1;
									for (int i = 0; i < elevatia.length; i++) {
										double difference = Math.abs(elevatia[i] - targetElevation);
										if (difference < minDifference) {
											minDifference = difference;
											elevation = elevatia[i];
											index = i;
										}
									}
									double distance = 0.0;
									double slope = 0.0;
									double finalSlope = 0.0;
									for(int i = 2;i < index - 2; i++){
										distance = calculateDistance(startX, endX);
										if(elevatia[i] > elevatia[i+1] && elevatia[i+1] <= elevatia[i+2]){
											distance = calculateDistance(startX, endX);
											slope = elevatia[i+1] / distance;
											finalSlope -= slope;
										}
										else if(elevatia[i] < elevatia[i+1] && elevatia[i+1] >= elevatia[i+2]){
											distance = calculateDistance(startX, endX);
											slope = elevatia[i+1] / distance;
											finalSlope += slope;
										}
									}
									return finalSlope;
								}
								private double calculareAscedent(int clickY){
									double ascedent = 0.0;
									double targetElevation = eleMax - ((clickY - 10) * (eleMax / 750.0));
									// Cautăm valoarea de elevație cea mai apropiată și returnăm-o
									double elevation = Double.MAX_VALUE;
									double minDifference = Double.MAX_VALUE;
									int index = -1;
									for (int i = 0; i < elevatia.length; i++) {
										double difference = Math.abs(elevatia[i] - targetElevation);
										if (difference < minDifference) {
											minDifference = difference;
											elevation = elevatia[i];
											index = i;
										}
									}
									for(int i = 2; i < index - 1; i++){
										if(elevatia[i] < elevatia[i+1]){
											ascedent += (elevatia[i+1] - elevatia[i]);
										}
									}
									return ascedent;
								}
								
								private double calculareDescedent(int clickY){
									double descedent = 0.0;
									double targetElevation = eleMax - ((clickY - 10) * (eleMax / 750.0));
									// Cautăm valoarea de elevație cea mai apropiată și returnăm-o
									double elevation = Double.MAX_VALUE;
									double minDifference = Double.MAX_VALUE;
									int index = -1;
									for (int i = 0; i < elevatia.length; i++) {
										double difference = Math.abs(elevatia[i] - targetElevation);
										if (difference < minDifference) {
											minDifference = difference;
											elevation = elevatia[i];
											index = i;
										}
									}
									for(int i = 2; i < index - 1; i++){
										if(elevatia[i] > elevatia[i+1]){
											descedent += (elevatia[i] - elevatia[i+1]);
										}
									}
									return descedent;
								}
								private double calculateCalorii(String gender, double age, double weight, double height, double backpack_weight, double distance, double avg_slope_procent, double moving_time_seconds){
									//int startX, endX, clickY;
									//distance = calculateDistance(startX,endX);
									//avg_slope_procent = calculateSlope(startX, endX, clickY);
									double calories = calorieCalculator.totalCalories(gender,age, weight, height, backpack_weight, distance, avg_slope_procent, moving_time_seconds);
									return calories;
								}
							});
							
							JPanel casetaPanel1 = new JPanel();
							casetaPanel1.setBackground(new Color(186,85,211)); // Setează culoarea de fundal a casetei
							
							Dimension screenSize2 = Toolkit.getDefaultToolkit().getScreenSize();
							int lungimeLaptop1 = screenSize1.width; // Obține lungimea ecranului
							casetaPanel1.setPreferredSize(new Dimension(lungimeLaptop, 67)); // Setează dimensiunea casetei
							
							casetaPanel1.setLayout(new FlowLayout(FlowLayout.CENTER, 100, 10));
							JLabel caseta6 = new JLabel("Distanta parcursa:");
							caseta6.setHorizontalAlignment(SwingConstants.LEFT); // Aliniază textul pe stanga orizontal
							caseta6.setVerticalAlignment(SwingConstants.CENTER); // Aliniază textul pe centru vertical
							
							JLabel caseta7 = new JLabel("Distanta ramasa:");
							caseta7.setHorizontalAlignment(SwingConstants.LEFT); // Aliniază textul pe stanga orizontal
							caseta7.setVerticalAlignment(SwingConstants.CENTER); // Aliniază textul pe centru vertical
							
							JLabel caseta8 = new JLabel("Elevatia:");
							caseta8.setHorizontalAlignment(SwingConstants.LEFT); // Aliniază textul pe stanga orizontal
							caseta8.setVerticalAlignment(SwingConstants.CENTER); // Aliniază textul pe centru vertical
							
							JLabel caseta9 = new JLabel("Panta:");
							caseta9.setHorizontalAlignment(SwingConstants.LEFT); // Aliniază textul pe stanga orizontal
							caseta9.setVerticalAlignment(SwingConstants.CENTER); // Aliniază textul pe centru vertical
							
							JLabel caseta10 = new JLabel("Ascendent:");
							caseta10.setHorizontalAlignment(SwingConstants.LEFT); // Aliniază textul pe stanga orizontal
							caseta10.setVerticalAlignment(SwingConstants.CENTER); // Aliniază textul pe centru vertical
							
							JLabel caseta11 = new JLabel("Descedent:");
							caseta11.setHorizontalAlignment(SwingConstants.LEFT); // Aliniază textul pe stanga orizontal
							caseta11.setVerticalAlignment(SwingConstants.CENTER); // Aliniază textul pe centru vertical

							casetaPanel1.add(caseta1);
							casetaPanel1.add(caseta2);
							casetaPanel1.add(caseta3);
							casetaPanel1.add(caseta4);
							casetaPanel1.add(caseta5);
							casetaPanel1.add(caseta6);
							
							caseta6.setBackground(new Color(255,192,203));
							caseta6.setOpaque(true); // Face caseta să fie vizibilă cu culoarea de fundal setată
							caseta6.setPreferredSize(new Dimension(150, 50)); // Setează dimensiunea casetei
							
							caseta7.setBackground(new Color(219,112,147));
							caseta7.setOpaque(true); // Face caseta să fie vizibilă cu culoarea de fundal setată
							caseta7.setPreferredSize(new Dimension(150, 50)); // Setează dimensiunea casetei
							
							caseta8.setBackground(new Color(218,112,214));
							caseta8.setOpaque(true); // Face caseta să fie vizibilă cu culoarea de fundal setată
							caseta8.setPreferredSize(new Dimension(150, 50)); // Setează dimensiunea casetei
							
							caseta9.setBackground(new Color(218,112,214));
							caseta9.setOpaque(true); // Face caseta să fie vizibilă cu culoarea de fundal setată
							caseta9.setPreferredSize(new Dimension(150, 50)); // Setează dimensiunea casetei
							
							caseta10.setBackground(new Color(218,112,214));
							caseta10.setOpaque(true); // Face caseta să fie vizibilă cu culoarea de fundal setată
							caseta10.setPreferredSize(new Dimension(150, 50)); // Setează dimensiunea casetei
							
							caseta11.setBackground(new Color(218,112,214));
							caseta11.setOpaque(true); // Face caseta să fie vizibilă cu culoarea de fundal setată
							caseta11.setPreferredSize(new Dimension(150, 50)); // Setează dimensiunea casetei
							
							casetaPanel1.revalidate();
							casetaPanel1.repaint();
							
							jp.setBackground(new Color(230,230,250)); // set the background color of the jp panel
							
							// Creează un obiect JFrame
							JFrame frame = new JFrame("Track Analyser");
							frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
							frame.getContentPane().setBackground( Color.blue );
							frame.setResizable(false);
							
							// Adauga panoul care afiseaza graficul la frame
							frame.add(jp, BorderLayout.CENTER);
							frame.add(casetaPanel1, BorderLayout.SOUTH); // Adaugă casetaPanel la partea de jos a frame-ului

							frame.setLocation(0, 0);
							frame.setVisible(true);				
							
							br.close();			
						} catch (Exception exception) {
							exception.printStackTrace();
						}
					}
				}
			} 
		}	
		//toScaleNumber: Numărul pe care doresc să-l scalez în intervalul specificat
		//Max: Valoarea maximă a intervalului în care doresc să scalez numărul
		//Min: Valoarea minimă a intervalului în care doresc să scalez numărul
		//scaleNumberMax: Valoarea maximă a intervalului din care provine numărul pe care doresc să-l scalezi.
		//De exemplu, dacă numărul inițial provine din intervalul 0-100 și doresc să-l scalez într-un interval 
		//de la 0 la 10, scaleNumberMax va fi 100.
		//scaleNumberMin: Valoarea minimă a intervalului din care provine numărul pe care doresc să-l scalez.
		//În exemplul anterior, scaleNumberMin ar fi 0
		
		public String getFileExtension(String filename) {
			int dotIndex = filename.lastIndexOf(".");
			if (dotIndex != -1 && dotIndex < filename.length() - 1) {
				return filename.substring(dotIndex + 1);
			}
			return "";
		}
		
		public int scaledNumber(int toScaleNumber, int Max, int Min, int scaleNumberMax, int scaleNumberMin){
			double scale = scaleNumberMax - scaleNumberMin;
			double output = Max - Min;
			double procent = (toScaleNumber - Min) / scale;
			double outputValue = (procent * output) + Min;
			return (int) outputValue;
		}		
		
		public static void main(String[] args) {    
			FileChooserGPX om=new FileChooserGPX();    
            om.setSize(500,500);    
            om.setLayout(null);    
            om.setVisible(true);    
            om.setDefaultCloseOperation(EXIT_ON_CLOSE);    
		}    
}  