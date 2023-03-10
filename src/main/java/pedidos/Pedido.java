package pedidos;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Pedido {
	private static final String COOKIE = "Cookie";
	private static final String X_FORM = "application/x-www-form-urlencoded";
	private static final String CONTENT_TYPE = "Content-Type";
	private static final String TABLE = "theDatatable";
	private static final String DESCRICAO = "pedido-ds_descricao_problema";
	private static final String DATA_KEY = "data-key";
	private static final String URL_VISUALIZAR = "url.visualizar";
	private static final String URL_PESQUISA = "url.pesquisa";
	private static final String H3 = "\n##############\n";
	private static final String H2 = "\n***********************\n";
	private static final String H1 = "\n\n\n!!!!!!!!!!!!!!!!!!\n";
	private static final String APPLICATION_PROPERTIES = "application.properties";

	public static void main(String[] args) throws IOException {
		System.out.println(new Date());
		String primeira = buscaPagina(1);
		String segunda = buscaPagina(2);
		FileOutputStream f = new FileOutputStream("resultado.txt");
		
		for ( int i = 3; !primeira.equals(segunda); i++) {
			f.write(primeira.getBytes());
			primeira = segunda;
			segunda = buscaPagina(i);
			f.flush();
		}
		System.out.println(new Date());
	}

	private static String buscaPagina(int pagina) throws IOException {

		Properties prop = new Properties();
		FileInputStream in = new FileInputStream(APPLICATION_PROPERTIES);
		prop.load(in);
		String cookie = prop.getProperty(COOKIE.toLowerCase());
		String urlPesquisa = prop.getProperty(URL_PESQUISA);
		String urlVisualizar = prop.getProperty(URL_VISUALIZAR);
		in.close();

		StringBuilder sb = new StringBuilder();
		sb.append(H1);
		
		getDocument(urlPesquisa + pagina, cookie).getElementById(TABLE).getElementsByAttribute(DATA_KEY).forEach(i -> {
			sb.append(H2);
			sb.append(i.text());
			sb.append(H3);
			try {
				sb.append(getDocument(urlVisualizar + i.attr(DATA_KEY), cookie).getElementById(DESCRICAO).text());
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		return sb.toString();
	}

	private static Document getDocument(String url, String cookie) throws IOException {
		Connection connection = Jsoup.connect(url);
		connection.header(CONTENT_TYPE, X_FORM);
		connection.header(COOKIE, cookie);
		return connection.get();
	}

}
