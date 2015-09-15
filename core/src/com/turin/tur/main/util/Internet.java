package com.turin.tur.main.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Net.HttpMethods;
import com.badlogic.gdx.net.HttpStatus;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.turin.tur.main.diseno.Enviables;



public class Internet {

	private static final String TAG = Internet.class.getName();
	public static boolean internetChecked=false;
	public static boolean serverOnline=false;
	public static String serverStatus="";
	public static HttpStatus serverStatusCode;
	public static boolean serverOk;
	public static final String server = "http://turintur.dynu.com/";
	
	public static void Check() {
		serverOnline = false; // Reinicia el status del server
		serverOk = false;
		internetChecked=true; // Indica que comenzo a chaequear
		new Thread(new Runnable() {

			@Override
			public void run() {

				String requestJson = "";
				
				final Net.HttpRequest request = new Net.HttpRequest(HttpMethods.GET);
				request.setContent(requestJson);

				request.setHeader("Content-Type", "application/json");
				request.setHeader("Accept", "application/json");
				request.setUrl(server+"status/");

				Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {

					@Override
					public void handleHttpResponse(Net.HttpResponse httpResponse) {

						serverOnline=true;
						serverStatus = httpResponse.getStatus().toString();
						serverStatusCode= httpResponse.getStatus();
						String rta = httpResponse.getResultAsString();
						if (rta.contains("on")) {
							serverOk=true;
							Gdx.app.debug(TAG, "Server ok");
						} else {
							serverOk = false;
							Gdx.app.debug(TAG, "Server no ok");
						}
						
					}

					@Override
					public void failed(Throwable t) {
						serverOnline=false;
						Gdx.app.debug(TAG, "Request Failed Completely");
					}

					@Override
					public void cancelled() {
						serverOnline=false;
						Gdx.app.debug(TAG, "request cancelled");
					}

				});

			}
		}).start();
		
	}
	
	public static void PUT(final Enviables objetoEnviado) {

		Array<String> urls = new Array<String>();
		urls.add("http://turintur.dynu.com/" + objetoEnviado.getClass().getSimpleName());
		//urls.add("http://181.169.225.117:3000/" + objetoEnviado.getClass().getSimpleName());

		if (objetoEnviado.contenidoLevel.size>0) {
			Gdx.app.debug(TAG, "Tamañan de datos level" + objetoEnviado.contenidoLevel.size);
		}
		
		for (final String url : urls) {

			new Thread(new Runnable() {

				@Override
				public void run() {

					Json json = new Json();
					json.setOutputType(OutputType.json);
					String requestJson = json.toJson(objetoEnviado.contenido);

					Net.HttpRequest request = new Net.HttpRequest(HttpMethods.POST);
					request.setContent(requestJson);

					request.setHeader("Content-Type", "application/json");
					request.setHeader("Accept", "application/json");
					request.setUrl(url);

					
					if (objetoEnviado.contenidoLevel.size>0) {
						Gdx.app.debug(TAG, "Contexto: " + objetoEnviado.levelLogHistory);
						Gdx.app.debug(TAG, "Json:" + requestJson);
					}
					Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {

						@Override
						public void handleHttpResponse(Net.HttpResponse httpResponse) {
							int statusCode = httpResponse.getStatus().getStatusCode();
							if (statusCode != HttpStatus.SC_CREATED) {
								Gdx.app.debug(TAG, "" + httpResponse.getStatus().getStatusCode());
								objetoEnviado.noEnviado();
								Gdx.app.debug(TAG, "Request Failed");
							} else {
								objetoEnviado.enviado();
							}
						}

						@Override
						public void failed(Throwable t) {
							objetoEnviado.noEnviado();
							Gdx.app.debug(TAG, "Request Failed Completely");
						}

						@Override
						public void cancelled() {
							objetoEnviado.noEnviado();
							Gdx.app.debug(TAG, "request cancelled");
						}

					});

				}
			}).start();
		}
	}
		
}
