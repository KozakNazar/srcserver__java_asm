/********************************************************************
* N.Kozak // Lviv'2019 // example server Java(LLNW)-Asm for pkt4 SP *
*                         file: Server.java                         *
*                                                            files: *
*                     JNICalcWrapper_gcc.s || JNICalcWrapper_vs.asm *
*                                                       Server.java *
*                                                       MANIFEST.MF *
*                                                                   *
*                                                 *extended version *
*********************************************************************/

//package sp2019.java;

import java.io.*;
import java.net.*;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Server {  
	static {	
		System.load(Paths.get("").toAbsolutePath().toString() + "\\JNICalcWrapper_gcc.so");				 
	}  
	  	  
	private static native double calc(double b2, float c1, double d2, float e1, double f2);	
	
	static boolean usePostSubmit = true; // defaault value

    static final int K = 0x00025630; // (153136) const
	static double b2 = 10.;          // defaault value
	static float c1 = 20.f;          // defaault value
	static double d2 = 30.;          // defaault value
	static float e1 = 40.f;          // defaault value
	static double f2 = 50.;          // defaault value
	
	private static String http_response_fmt =
		  "HTTP / 1.1 200 OK\r\n" +
		  "Date : Mon, 27 Jul 2009 12 : 28 : 53 GMT\r\n" +
		  "Server : Apache / 2.2.14 (Win32)\r\n" +
		  "Last - Modified : Wed, 22 Jul 2009 19 : 15 : 56 GMT\r\n" +
		  "Content - Length : %d\r\n" +
		  "Content - Type : text / html\r\n" +
		  "Connection : Closed\r\n" +
		  "\r\n" +
		  "%s"
		  ;
  
	private static String http_response_server_stopped =
		  "HTTP / 1.1 200 OK\r\n" +
		  "Date : Mon, 27 Jul 2009 12 : 28 : 53 GMT\r\n" +
		  "Server : Apache / 2.2.14 (Win32)\r\n" +
		  "Last - Modified : Wed, 22 Jul 2009 19 : 15 : 56 GMT\r\n" +
		  "Content - Length : 57\r\n" +
		  "Content - Type : text / html\r\n" +
		  "Connection : Closed\r\n" +
		  "\r\n" +
		  "<html>\r\n" +
		  "<body>\r\n" +
		  "<h1>" + "Server stopped" + "</h1>\r\n" +
		  "</body>\r\n" +
		  "</html>"
		  ;  
  
	private static String html_code_fmt__withGetSubmit =
		  "<html>\r\n" +
		  "<head>\r\n" +
		  "<link rel = \"icon\" href = \"data:;base64,=\">\r\n" +
		  "</head>\r\n" +
		  "<body>\r\n" +
		  "\r\n" +
		  "<form action = \"/setSettings\" method=\"get\">\r\n" +
		  "<h1>Settings:</h1>\r\n" +
		  "<p>Select mode:</p>\r\n" +
		  "<input type = \"radio\" name = \"mode\" value = \"1\"> mode 1<br>\r\n" +
		  "<input type = \"radio\" name = \"mode\" value = \"2\"> mode 2<br>\r\n" +
		  "<input type = \"radio\" name = \"mode\" value = \"3\"> mode 3<br>\r\n" +
		  "<p>Change used http - method : </p>\r\n" +
		  "<input type = \"radio\" name = \"http_method\" value = \"0\" checked = \"checked\"> GET<br>\r\n" +
		  "<input type = \"radio\" name = \"http_method\" value = \"1\"> POST<br>\r\n" +
		  "\r\n" +
		  "<input type = \"submit\" value = \"Submit parameters and reload page\" text = \"\">\r\n" +
		  "</form>\r\n" +
		  "<h1>Compute board:</h1>\r\n" +
		  "<button type = \"submit\" form = \"calcData\">Send values by GET and compute result</button>\r\n" +
		  "\r\n" +
		  "<form id = \"calcData\"  method = \"get\" action = \"callCalc\">\r\n" +
		  "\r\n" +
		  "<h2>X = K + B2 - D2/C1 + E1*F2</h2>\r\n" +
		  "<h2>--------------------------</h2>\r\n" +
		  "<h2>K = %d</h2>\r\n" +
		  "<h2>B = <input name = \"B\" type = \"text\" value = \"%f\"></h2>\r\n" +
		  "<h2>C = <input name = \"C\" type = \"text\" value = \"%f\"></h2>\r\n" +
		  "<h2>D = <input name = \"D\" type = \"text\" value = \"%f\"></h2>\r\n" +
		  "<h2>E = <input name = \"E\" type = \"text\" value = \"%f\"></h2>\r\n" +
		  "<h2>F = <input name = \"F\" type = \"text\" value = \"%f\"></h2>\r\n" +
		  "<h2>-------</h2>\r\n" +
		  "<h2>X(Assembly) = %f</h2>\r\n" +
		  "<h2>X(Java) = %f</h2>\r\n" +
		  "<h2>--------------------------</h2>\r\n" +
		  "<input type = \"submit\" value = \"Send values by GET and compute result\">\r\n" +
		  "\r\n" +
		  "</form>\r\n" +
		  "</body>\r\n" +
		  "</html>\r\n"
		  ;
	
	private static String html_code_fmt__withPostSubmit =
		  "<html>\r\n" +
		  "<head>\r\n" +
		  "<link rel = \"icon\" href = \"data:;base64,=\">\r\n" +
		  "</head>\r\n" +
		  "<body>\r\n" +
		  "\r\n" +
		  "<form action = \"/setSettings\" method=\"post\">\r\n" +
		  "<h1>Settings:</h1>\r\n" +
		  "<p>Select mode:</p>\r\n" +
		  "<input type = \"radio\" name = \"mode\" value = \"1\"> mode 1<br>\r\n" +
		  "<input type = \"radio\" name = \"mode\" value = \"2\"> mode 2<br>\r\n" +
		  "<input type = \"radio\" name = \"mode\" value = \"3\"> mode 3<br>\r\n" +
		  "<p>Change used http - method : </p>\r\n" +
		  "<input type = \"radio\" name = \"http_method\" value = \"0\"> GET<br>\r\n" +
		  "<input type = \"radio\" name = \"http_method\" value = \"1\" checked = \"checked\"> POST<br>\r\n" +
		  "\r\n" +
		  "<input type = \"submit\" value = \"Submit parameters and reload page\" text = \"\">\r\n" +
		  "</form>\r\n" +
		  "<h1>Compute board:</h1>\r\n" +
		  "<button type = \"submit\" form = \"calcData\">Send values by POST and compute result</button>\r\n" +
		  "\r\n" +
		  "<form id = \"calcData\"  method = \"post\" action = \"callCalc\">\r\n" +
		  "\r\n" +
		  "<h2>X = K + B2 - D2/C1 + E1*F2</h2>\r\n" +
		  "<h2>--------------------------</h2>\r\n" +
		  "<h2>K = %d</h2>\r\n" +
		  "<h2>B = <input name = \"B\" type = \"text\" value = \"%f\"></h2>\r\n" +
		  "<h2>C = <input name = \"C\" type = \"text\" value = \"%f\"></h2>\r\n" +
		  "<h2>D = <input name = \"D\" type = \"text\" value = \"%f\"></h2>\r\n" +
		  "<h2>E = <input name = \"E\" type = \"text\" value = \"%f\"></h2>\r\n" +
		  "<h2>F = <input name = \"F\" type = \"text\" value = \"%f\"></h2>\r\n" +
		  "<h2>-------</h2>\r\n" +
		  "<h2>X(Assembly) = %f</h2>\r\n" +
		  "<h2>X(Java) = %f</h2>\r\n" +
		  "<h2>--------------------------</h2>\r\n" +
		  "<input type = \"submit\" value = \"Send values by POST and compute result\">\r\n" +
		  "\r\n" +
		  "</form>\r\n" +
		  "</body>\r\n" +
		  "</html>\r\n"
		  ;
  
	private static String buildResponse() {
		double x_AssemblyResult = calc(b2, c1, d2, e1, f2);
		double x_CResult = ((double) K + b2 - d2 / (double) c1 + (double) e1 * f2);

		String html_code_fmt = html_code_fmt__withGetSubmit;

		if (usePostSubmit) {
			html_code_fmt = html_code_fmt__withPostSubmit;
		}

		String html_code = String.format(html_code_fmt, K, b2, c1, d2, e1, f2, x_AssemblyResult, x_CResult);
		return String.format(http_response_fmt, html_code.length(), html_code);
	}
  
	private static String read(Socket client) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
		StringBuilder stringBuilder = new StringBuilder();
		do { // (!)
			stringBuilder.append((char) reader.read());
		} while (reader.ready());
		return stringBuilder.toString();
	} 

	private static int handleClient(Socket connectionSocket) throws Exception {
		String message = read(connectionSocket);

		System.out.println("*Received: " + message);
		if (message == null || message.length() <= 0) {
			connectionSocket.getOutputStream().write(buildResponse().getBytes());
			connectionSocket.getOutputStream().flush();
			System.out.printf("\r\n>> response sent\r\n");

			connectionSocket.close();
			return 0;
		}
		System.out.println("Received: " + message);

		if (message.indexOf("close") > -1) {
			connectionSocket.getOutputStream().write(http_response_server_stopped.getBytes());
			connectionSocket.getOutputStream().flush();
			System.out.printf("\r\n>> Server stopped!\r\n");
			connectionSocket.close();
			return -1;// break;
		}

		final String http_method_key = "http_method=";
		final String B_key = "B=";
		final String C_key = "C=";
		final String D_key = "D=";
		final String E_key = "E=";
		final String F_key = "F=";

		Matcher matcher;
		Pattern patternIntegerValue = Pattern.compile("[-+]?(0[xX][\\dA-Fa-f]+|0[0-7]*|\\d+)");
		Pattern patternRealValue = Pattern.compile("[-+]?(\\d+([.,]\\d*)?|[.,]\\d+)([eE][-+]?\\d+)?");

		int indexPOSTValues;
		if (message.indexOf("POST") > -1 && (indexPOSTValues = message.indexOf("\r\n\r\n")) > -1) {
			usePostSubmit = true;

			int index;
			if ((index = message.substring(indexPOSTValues).indexOf(http_method_key)) > -1) {
				int usePostSubmitValue = 0;
				if (usePostSubmit) {
					usePostSubmitValue = 1;
				}
				if ((matcher = patternIntegerValue.matcher(message.substring(indexPOSTValues + index + http_method_key.length()))).find()) {
					usePostSubmitValue = Integer.parseInt(message.substring(indexPOSTValues + index + http_method_key.length() + matcher.start(), indexPOSTValues + index + http_method_key.length() + matcher.end()));
				}
				usePostSubmit = true;
				if (usePostSubmitValue == 0) {
					usePostSubmit = false;
				}
			}

			if ((index = message.substring(indexPOSTValues).indexOf(B_key)) > -1) {
				if ((matcher = patternRealValue.matcher(message.substring(indexPOSTValues + index + B_key.length()))).find()) {
					b2 = Double.parseDouble(message.substring(indexPOSTValues + index + B_key.length() + matcher.start(), indexPOSTValues + index + B_key.length() + matcher.end()));
				} else {
					b2 = 0.;
				}
			}
			if ((index = message.substring(indexPOSTValues).indexOf(C_key)) > -1) {
				if ((matcher = patternRealValue.matcher(message.substring(indexPOSTValues + index + C_key.length()))).find()) {
					c1 = Float.parseFloat(message.substring(indexPOSTValues + index + C_key.length() + matcher.start(), indexPOSTValues + index + C_key.length() + matcher.end()));
				} else {
					c1 = 0.f;
				}
			}
			if ((index = message.substring(indexPOSTValues).indexOf(D_key)) > -1) {
				if ((matcher = patternRealValue.matcher(message.substring(indexPOSTValues + index + D_key.length()))).find()) {
					d2 = Double.parseDouble(message.substring(indexPOSTValues + index + D_key.length() + matcher.start(), indexPOSTValues + index + D_key.length() + matcher.end()));
				} else {
					d2 = 0.;
				}
			}
			if ((index = message.substring(indexPOSTValues).indexOf(E_key)) > -1) {
				if ((matcher = patternRealValue.matcher(message.substring(indexPOSTValues + index + E_key.length()))).find()) {
					e1 = Float.parseFloat(message.substring(indexPOSTValues + index + E_key.length() + matcher.start(), indexPOSTValues + index + E_key.length() + matcher.end()));
				} else {
					e1 = 0.f;
				}
			}
			if ((index = message.substring(indexPOSTValues).indexOf(F_key)) > -1) {
				if ((matcher = patternRealValue.matcher(message.substring(indexPOSTValues + index + F_key.length())))
						.find()) {
					f2 = Double.parseDouble(message.substring(indexPOSTValues + index + F_key.length() + matcher.start(), indexPOSTValues + index + F_key.length() + matcher.end()));
				} else {
					f2 = 0.;
				}
			}
		} else {

			int index;
			if ((index = message.indexOf(http_method_key)) > -1) {
				int usePostSubmitValue = 0;
				if (usePostSubmit) {
					usePostSubmitValue = 1;
				}
				if ((matcher = patternIntegerValue.matcher(message.substring(index + http_method_key.length()))).find()) {
					usePostSubmitValue = Integer.parseInt(message.substring(index + http_method_key.length() + matcher.start(), index + http_method_key.length() + matcher.end()));
				}
				usePostSubmit = true;
				if (usePostSubmitValue == 0) {
					usePostSubmit = false;
				}
			} else {

				if ((index = message.indexOf(B_key)) > -1) {
					usePostSubmit = false;
					if ((matcher = patternRealValue.matcher(message.substring(index + B_key.length()))).find()) {
						b2 = Double.parseDouble(message.substring(index + B_key.length() + matcher.start(), index + B_key.length() + matcher.end()));
					} else {
						b2 = 0.;
					}
				}
				if ((index = message.indexOf(C_key)) > -1) {
					usePostSubmit = false;
					if ((matcher = patternRealValue.matcher(message.substring(index + C_key.length()))).find()) {
						c1 = Float.parseFloat(message.substring(index + C_key.length() + matcher.start(), index + C_key.length() + matcher.end()));
					} else {
						c1 = 0.f;
					}
				}
				if ((index = message.indexOf(D_key)) > -1) {
					usePostSubmit = false;
					if ((matcher = patternRealValue.matcher(message.substring(index + D_key.length()))).find()) {
						d2 = Double.parseDouble(message.substring(index + D_key.length() + matcher.start(), index + D_key.length() + matcher.end()));
					} else {
						d2 = 0.;
					}
				}
				if ((index = message.indexOf(E_key)) > -1) {
					usePostSubmit = false;
					if ((matcher = patternRealValue.matcher(message.substring(index + E_key.length()))).find()) {
						e1 = Float.parseFloat(message.substring(index + E_key.length() + matcher.start(), index + E_key.length() + matcher.end()));
					} else {
						e1 = 0.f;
					}
				}
				if ((index = message.indexOf(F_key)) > -1) {
					usePostSubmit = false;
					if ((matcher = patternRealValue.matcher(message.substring(index + F_key.length()))).find()) {
						f2 = Double.parseDouble(message.substring(index + F_key.length() + matcher.start(), index + F_key.length() + matcher.end()));
					} else {
						f2 = 0.;
					}
				}
			}
		}

		connectionSocket.getOutputStream().write(buildResponse().getBytes());// outToClient.writeBytes(buildResponse());
		connectionSocket.getOutputStream().flush();
		System.out.printf("\r\n>> response sent\r\n");
		connectionSocket.close();

		return 0;
	} 
	
	private static ServerSocket serverSocket = null;

	public static void main(String[] args) throws Exception {
		serverSocket = new ServerSocket(80);

		int toExit = 0;
		while (toExit == 0) {
			System.out.printf("\r\n>> wait for new connection(server unlock)\r\n");
			Socket connectionSocket = serverSocket.accept();
			if (connectionSocket.isConnected()) {
				System.out.printf("\r\n>> received new request(server lock, used one thread)\r\n");
				toExit = handleClient(connectionSocket);
				System.out.printf("\r\n>> request processed\r\n");
			}
		}
	}

	protected void finalize() throws IOException {
		if (serverSocket != null) {
			serverSocket.close();
		}
	}
}