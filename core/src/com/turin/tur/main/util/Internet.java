package com.turin.tur.main.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Net.HttpMethods;
import com.badlogic.gdx.net.HttpStatus;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;

public class Internet {

	private static final String TAG = Internet.class.getName();

	void enviado(Object clase, String mensaje) {
		// TODO Auto-generated method stub

	}

	public static void PUT(final Enviable clase) {

		Array<String> urls = new Array<String>();
		urls.add("http://localhost:3000/" + clase.getClass().getSimpleName());
		// urls.add("http://181.169.225.117:3000/" + clase.getClass().getSimpleName());

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
		public abstract void enviado();

		public abstract void noEnviado();
	}
}
