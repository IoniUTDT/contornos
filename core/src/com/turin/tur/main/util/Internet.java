package com.turin.tur.main.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Net.HttpMethods;
import com.badlogic.gdx.net.HttpStatus;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.turin.tur.main.diseno.Session.JsonSessionLog;

public class Internet {

	private static final String TAG = Internet.class.getName();

	void enviado(Object clase, String mensaje) {
		// TODO Auto-generated method stub

	}

	public static void PUT(final Enviable clase) {

		Array<String> urls = new Array<String>();
		urls.add("http://localhost:3000/" + clase.getClass().getSimpleName());
		urls.add("http://181.169.225.117:3000/" + clase.getClass().getSimpleName());

		for (final String url : urls) {

			new Thread(new Runnable() {

				@Override
				public void run() {

					Json json = new Json();
					json.setOutputType(OutputType.json);
					String requestJson = json.toJson(clase);

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
								clase.noEnviado();
								System.out.println("Request Failed");
							} else {
								clase.enviado();
							}
						}

						public void failed(Throwable t) {
							clase.noEnviado();
							System.out.println("Request Failed Completely");
						}

						@Override
						public void cancelled() {
							clase.noEnviado();
							System.out.println("request cancelled");
						}

					});

				}
			}).start();
		}

	}

	public static abstract class Enviable {
		public abstract String path();
		public abstract Object getObject();
		public abstract Class getMyclass ();
		
		public void enviar() {
			// Esta rutina intenta subir los datos de la session al servidor. En funcion del resultado se activa enviado o no enviado (esto sucede en la funcion put de la clase internet)
			this.load();
			Internet.PUT(this);	
		}
		private <getMyclass> getMyclass load() {
			String savedData = FileHelper.readLocalFile(this.path());
			if (!savedData.isEmpty()) {
				Json json = new Json();
				return (getMyclass) json.fromJson(getMyclass(),savedData);
			} else {
				Gdx.app.error(TAG, "No se a podido encontrar la info del historial de sesiones");
			}
			return (getMyclass) new JsonSessionLog();
		}
	}
}
