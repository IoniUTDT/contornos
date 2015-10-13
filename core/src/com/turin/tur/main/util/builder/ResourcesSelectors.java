package com.turin.tur.main.util.builder;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.turin.tur.main.diseno.ExperimentalObject.JsonResourcesMetaData;
import com.turin.tur.main.util.FileHelper;
import com.turin.tur.main.util.Constants.Resources;
import com.turin.tur.main.util.Constants.Resources.Categorias;


public class ResourcesSelectors {

	static Array<JsonResourcesMetaData> listadoRecursos = Builder.listadoRecursos;
	static Array<Array<Integer>> listadosId = Builder.listadosId;
	static Array<Agrupamientos> listadosGrupos = Builder.listadosGrupos;
	
	public static int[] rsGetAllGrupo(String agrupamientoPedido) {
		int[] recursos = new int[] {0,0,0,0,0,0}; //Inicializa el vector con datos nulos, total solo puede tener 6 elementos
		for (Agrupamientos agrupamiento : listadosGrupos) {
			if (agrupamiento.nombre.equals(agrupamientoPedido)) {
				if (agrupamiento.ids.size==6) {
					for (int i=0; i<6; i++) {
						recursos[i] = agrupamiento.ids.get(i); 
					}
					return recursos;
				} 
			}
		}
		System.out.println("Se ha solicitado un recurso del grupo "+agrupamientoPedido+" y no se ha podido encontrar dicho agrupamiento. Se devuelve un elemento nulo.");
		return null;
	}

	/**
	 * 
	 * @param agrupamientoPedido 
	 *			Indica el nombre del agrupamiento del que se quiere extraer un elemento.
	 * @param categoria
	 * 			Indica la categoria a la que debe pertenecer dicho elemento.
	 * 
	 * @return
	 * 			Devuelve el int con el id del elemento seleccionado o 0 si no se encuetra ninguno.
	 */
	public static int rsGetGrupo(String agrupamientoPedido, Categorias categoria) {
		int recurso;
		recurso=0;
		for (Agrupamientos agrupamiento : listadosGrupos) {
			if (agrupamiento.nombre.equals(agrupamientoPedido)) {
				Array<Integer> listadoValido = new Array<Integer>();
				for (int i=0;i<agrupamiento.ids.size;i++) {
					int elemento = agrupamiento.ids.get(i);
					// Carga la info de la metada 
					String savedData = FileHelper.readFile(Resources.Paths.fullCurrentVersionPath + elemento + ".meta");
					Json json = new Json();
					JsonResourcesMetaData jsonMetaData =  json.fromJson(JsonResourcesMetaData.class, savedData);
					// Lo agrega a la lista valida si corresponde 
					if (jsonMetaData.categories.contains(categoria, false)) {
						listadoValido.add(elemento);
					}
				}
				if (listadoValido.size!=0) {
					recurso = listadoValido.random();
					return recurso;
				}
			}
		}
		System.out.println("Se ha solicitado un recurso del grupo "+agrupamientoPedido+" y no se ha podido encontrar dicho agrupamiento. Se devuelve un elemento nulo.");
		return recurso;
	}
	
	public static int rsGetGrupo(String agrupamientoPedido, int omitir) {
		int recurso;
		recurso=0;
		for (Agrupamientos agrupamiento : listadosGrupos) {
			if (agrupamiento.nombre.equals(agrupamientoPedido)) {
				for (int i=0;i<agrupamiento.ids.size;i++) {
					recurso = agrupamiento.ids.random();
					if (recurso != omitir) {
						return recurso;
					}
				}
			}
		}
		System.out.println("Se ha solicitado un recurso del grupo "+agrupamientoPedido+" y no se ha podido encontrar dicho agrupamiento. Se devuelve un elemento nulo.");
		return recurso;
	}

	public static int rsGetGrupo (String agrupamientoPedido) {
		int recurso;
		recurso=0;
		for (Agrupamientos agrupamiento : listadosGrupos) {
			if (agrupamiento.nombre.equals(agrupamientoPedido)) {
				recurso = agrupamiento.ids.random();
				return recurso;
			}
		}
		System.out.println("Se ha solicitado un recurso del grupo "+agrupamientoPedido+" y no se ha podido encontrar dicho agrupamiento. Se devuelve un elemento nulo.");
		return recurso;
	}

	public static int rsGet(Categorias categoria) {
		return rsGet(categoria,0);
	}

	public static int rsGet(Categorias categoria, int omitir) {
		int seleccionado;
		for (int i=1; i<100; i++) {
			seleccionado = listadosId.get(categoria.ID).get(MathUtils.random(listadosId.get(categoria.ID).size-1));
			if (seleccionado != omitir) {
				return seleccionado;
			}
		}
		System.out.println("No se puedo seleccionar un recuros de la categoria "+categoria.texto+" que sea diferente a la "+omitir+" en al menos 100 intentos");
		return 0;
	}
	
	public static int rsGet(Categorias categoria, Categorias categoria2) {
		return rsGet(categoria, categoria2, 0);
	}
	
	public static int rsGet(Categorias categoria, Categorias categoria2, int omitir) {
		
		for (int i=0;i<100;i++) {
			int elemento = listadosId.get(categoria.ID).get(MathUtils.random(listadosId.get(categoria.ID).size-1));
			// Carga la info de la metada 
			String savedData = FileHelper.readFile(Resources.Paths.fullCurrentVersionPath + elemento + ".meta");
			Json json = new Json();
			JsonResourcesMetaData jsonMetaData =  json.fromJson(JsonResourcesMetaData.class, savedData);
			if (jsonMetaData.categories.contains(categoria2, false)) {
				if (elemento != omitir) { 
					return elemento;
				}
			}
		}
		System.out.println("Imposible conseguir un " + categoria.texto + " que sea " + categoria2.texto + " y sea diferente al elemento "+omitir+" en al menos 100 intentos");
		return 0;
	}
	
	public static class Agrupamientos {
		public String nombre;
		public Array<Integer> ids = new Array<Integer>();
	}
}
