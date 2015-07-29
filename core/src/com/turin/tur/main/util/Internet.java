package com.turin.tur.main.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Net.HttpMethods;
import com.badlogic.gdx.net.HttpStatus;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.turin.tur.main.diseno.Session.JsonSessionHistory;

public class Internet {

	private static final String TAG = Internet.class.getName();

	public static void PUT(final Enviable objetoEnviado) {

		Array<String> urls = new Array<String>();
		urls.add("http://localhost:3000/" + objetoEnviado.getClass().getSimpleName());
		urls.add("http://181.169.225.117:3000/" + objetoEnviado.getClass().getSimpleName());

		for (final String url : urls) {

			new Thread(new Runnable() {

				@Override
				public void run() {

					Json json = new Json();
					json.setOutputType(OutputType.json);
					String requestJson = json.toJson(objetoEnviado);

					Net.HttpRequest request = new Net.HttpRequest(HttpMethods.POST);
					request.setContent(requestJson);

					request.setHeader("Content-Type", "application/json");
					request.setHeader("Accept", "application/json");
					request.setUrl(url);

					Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {

						public void handleHttpResponse(Net.HttpResponse httpResponse) {

							int statusCode = httpResponse.getStatus().getStatusCode();
							System.out.println(url);
							System.out.println(httpResponse.getStatus().getStatusCode());
							if (statusCode != HttpStatus.SC_CREATED) {
								Gdx.app.debug(TAG, "" + httpResponse.getStatus().getStatusCode());
								objetoEnviado.noEnviado();
								System.out.println("Request Failed");
							} else {
								objetoEnviado.enviado();
							}
						}

						public void failed(Throwable t) {
							objetoEnviado.noEnviado();
							System.out.println("Request Failed Completely");
						}

						@Override
						public void cancelled() {
							objetoEnviado.noEnviado();
							System.out.println("request cancelled");
						}

					});

				}
			}).start();
		}
	}
		
	/*
	 * Este metodo tiene que cargar el objeto 
	 */
	public static void Enviar (Class clase, Enviable objeto, String path) throws ClassNotFoundException {
		objeto = Internet.Load(clase,path);
		Internet.PUT(objeto);
	}
		
	private static Enviable Load(Class clase, String path) throws ClassNotFoundException { //Revisar bien que onda el throws!
		String savedData = FileHelper.readLocalFile(path);
		if (!savedData.isEmpty()) {
			Json json = new Json();
			return json.fromJson(clase, savedData);
		} else {
			Gdx.app.error(TAG, "No se a podido encontrar la info del historial de sesiones");
		}
		return new contenidoVacio();
	}

	public static abstract class Enviable {
		public abstract void enviado();
		public abstract void noEnviado();
	}
	
	public static class contenidoVacio extends Enviable {

		@Override
		public void enviado() {
			Gdx.app.error(TAG, "Error al generar el contenido a enviar. Se ha generado un envio vacio");
		}

		@Override
		public void noEnviado() {
			Gdx.app.error(TAG, "Error al generar el contenido a enviar. Se ha generado un envio vacio");	
		}
		
	}
}
