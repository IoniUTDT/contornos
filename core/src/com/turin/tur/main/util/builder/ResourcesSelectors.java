package com.turin.tur.main.util.builder;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.turin.tur.main.diseno.ExperimentalObject.JsonResourcesMetaData;
import com.turin.tur.main.util.FileHelper;
import com.turin.tur.main.util.builder.LevelMaker.Dificultad;
import com.turin.tur.main.util.Constants.Resources;
import com.turin.tur.main.util.Constants.Resources.Categorias;


public class ResourcesSelectors {

	static Array<JsonResourcesMetaData> listadoRecursos = Builder.listadoRecursos;
	static Array<Array<Integer>> listadosId = Builder.listadosId;
	static Array<Agrupamientos> listadosGrupos = Builder.listadosGrupos;
	
	/**
	 * Esta funcion selecciona el grupo de 6 elementos que pertenecen al agrupamiento pedido. Si no encuentra un grupo de 6 elementos devuelve un objeto vacio y manda un mensaje de error.
	 * Los elementos se filtran por el nivel de dificultad seteado en la variable global de la clase, un -1 significa sin dificultad asignada y es compatible con cualquier dificultad.
	 * 
	 * @param agrupamientoPedido
	 * 	Representa el nombre del agrupamiento pedido.
	 * @return
	 * 	Lista de ids de los elementos pertenecientes al agrupamiento pedido. 
	 */
	public static int[] rsGetAllGrupo(String agrupamientoPedido, Dificultad dificultad) {
		int[] recursos = new int[] {0,0,0,0,0,0}; //Inicializa el vector con datos nulos, total solo puede tener 6 elementos
		for (Agrupamientos agrupamiento : listadosGrupos) {
			if (agrupamiento.nombre.equals(agrupamientoPedido)) {
				Agrupamientos agrupamientoFiltroDificultad = new Agrupamientos();
				agrupamientoFiltroDificultad.nombre = agrupamiento.nombre;
				if (dificultad.dificultad != -1) { // Filtra solo los de la dificultad indicada si no es -1;
					for (int i=0; i<agrupamiento.ids.size;i++) {
						// Carga la info de la metada 
						String savedData = FileHelper.readFile(Resources.Paths.fullCurrentVersionPath + agrupamiento.ids.get(i) + ".meta");
						Json json = new Json();
						JsonResourcesMetaData jsonMetaData =  json.fromJson(JsonResourcesMetaData.class, savedData);
						if ((jsonMetaData.nivelDificultad == dificultad.dificultad) || (jsonMetaData.nivelDificultad==-1)){ // Lo incluye sea -1 o el pedido
							agrupamientoFiltroDificultad.ids.add(agrupamiento.ids.get(i));
						}
					}
				} else {
					agrupamientoFiltroDificultad = agrupamiento;
				}
				if (agrupamientoFiltroDificultad.ids.size==6) {
					for (int i=0; i<6; i++) {
						recursos[i] = agrupamientoFiltroDificultad.ids.get(i); 
					}
					return recursos;
				} 
			}
		}
		System.out.println("Se ha solicitado un recurso del grupo "+agrupamientoPedido+" y no se ha podido encontrar dicho agrupamiento. Se devuelve un elemento nulo.");
		return null;
	}
	public static int[] rsGetAllGrupo(String agrupamientoPedido) {
		Dificultad dificultad = new Dificultad(-1);
		return rsGetAllGrupo(agrupamientoPedido, dificultad);
	}

	/**
	 * Esta funcion devuelve un elemento random pertenezca a la categoria pedida, al agrupamiento pedido, que no este en la lista de elementos omitidos y que tenga el nivel de dificultad deseado
	 * La funcion se puede invocar con todos los parametros o usando algunos de los equivalentes con menos parametros. Estos invocan la funcion con parametros que anulan el filtro.
	 * 
	 *  Anulan el filtro el string agrupamientoPedido="SinAgrupamiento"
	 *  Anulan el filtro la categoria = Categorias.Nada
	 *  Anulan el filtro int[] = {}
	 *  Anulan el filtro nivelDificultad = -1 
	 * 
	 * @param agrupamientoPedido 
	 *			Indica el nombre del agrupamiento del que se quiere extraer un elemento.
	 * @param categoria
	 * 			Indica la categoria a la que debe pertenecer dicho elemento.
	 * @param nivelDificultad
	 * 			Indica el nivel de dificultad a seleccionar, -1 implica seleccionar cualquier dificultad.
	 * @param omitir 
	 * 			Indica que recursos se deben omitir
	 * 
	 * @return
	 * 			Devuelve el int con el id del elemento seleccionado o 0 si no se encuetra ninguno.
	 */
	public static int rsGet(String agrupamientoPedido, Array<Categorias> categorias, int[] omitir, Dificultad dificultad) {
		int recurso;
		recurso=0;
		Array<Integer> listadoValido = new Array<Integer>();
		
		for (JsonResourcesMetaData json: listadoRecursos) {
			if (json.idVinculo==null) { // Evita que haya un error de null
				json.idVinculo = "sin dato";
			}
			if ((json.idVinculo.equals(agrupamientoPedido)) || (agrupamientoPedido=="SinAgrupamiento")) {
				int elemento = json.resourceId.id;
				// Lo agrega a la lista valida si corresponde
				if ((dificultad.dificultad==-1) || (json.nivelDificultad==dificultad.dificultad) || (json.nivelDificultad==-1)) {
					if (categorias.contains(Categorias.Nada, false)) {
						listadoValido.add(elemento);
					} else {
						boolean contieneTodasLasCategorias = true;
						for (Categorias categoria:categorias) { 
							if (!json.categories.contains(categoria, false)){
								contieneTodasLasCategorias = false;
								break;
							}
						}
						if (contieneTodasLasCategorias) {
							listadoValido.add(elemento);
						}
					}
				}
			}
		}
		// Hasta aca deberia haber recolectado todos los recursos validos que pertenecen a la categoria, grupo y dificultad indicada
		
		// Ahora elegimos uno al azar que no este en la lista de omitidos
		if (listadoValido.size!=0) {
			listadoValido.shuffle();
			for (int i=0;i<listadoValido.size;i++) {
				recurso = listadoValido.get(i);
				boolean valido = true;
				for (int j=0; j<omitir.length; j++) {
					if (recurso==omitir[j]) {
						valido=false;
					}
				}
				if (valido) {
					System.out.print(".");
					return recurso;
				}
			}
		} else {
			System.out.println("Se ha solicitado un recurso del grupo "+agrupamientoPedido+", de las categorias "+categorias+", de dificultad "+dificultad.dificultad+" , y que no sea alguno de los siguientes elementos "+omitir+". No se puede encontrar!");
		}
		System.out.print(".");
		return recurso;
	}

	
	/*
	 * 
	 * Aca van varios invocadores (o como se diga) con diferentes tipos de parametros
	 * 
	 */
	public static int rsGet (String agrupamientoPedido) {
		Array<Categorias> categorias = new Array<Categorias>();
		categorias.add(Categorias.Nada);
		int [] omitir = {};
		Dificultad dificultad = new Dificultad(-1);
		return rsGet(agrupamientoPedido, categorias, omitir, dificultad);
	}
	public static int rsGet (String agrupamientoPedido, Dificultad dificultad) { //Ok
		Array<Categorias> categorias = new Array<Categorias>();
		categorias.add(Categorias.Nada);
		int [] omitir = {};
		return rsGet(agrupamientoPedido, categorias, omitir, dificultad);
	}
	public static int rsGet (String agrupamientoPedido, Categorias categoria, Dificultad dificultad) { //Ok
		Array<Categorias> categorias = new Array<Categorias>();
		categorias.add(categoria);
		int [] omitir = {};
		return rsGet(agrupamientoPedido, categorias, omitir, dificultad);
	}
	public static int rsGet (String agrupamientoPedido, int omitir) {
		Array<Categorias> categorias = new Array<Categorias>();
		categorias.add(Categorias.Nada);
		int [] omitirArray = {omitir};
		Dificultad dificultad = new Dificultad(-1);
		return rsGet(agrupamientoPedido, categorias, omitirArray, dificultad);
	}	
	public static int rsGet (String agrupamientoPedido, int omitir, Dificultad dificultad) { //Ok
		Array<Categorias> categorias = new Array<Categorias>();
		categorias.add(Categorias.Nada);
		int [] omitirArray = {omitir};
		return rsGet(agrupamientoPedido, categorias, omitirArray, dificultad);
	}
	public static int rsGet (Categorias categoria, Dificultad dificultad) { //Ok
		String agrupamientoPedido = "SinAgrupamiento";
		Array<Categorias> categorias = new Array<Categorias>();
		categorias.add(categoria);
		int [] omitir = {};
		return rsGet(agrupamientoPedido, categorias, omitir, dificultad);
	}
	public static int rsGet (Categorias categoria, Categorias categoria2, Dificultad dificultad) { //Ok
		String agrupamientoPedido = "SinAgrupamiento";
		Array<Categorias> categorias = new Array<Categorias>();
		categorias.add(categoria);
		categorias.add(categoria2);
		int [] omitir = {};
		return rsGet(agrupamientoPedido, categorias, omitir, dificultad);
	}
	public static int rsGet (Categorias categoria, int omitir, Dificultad dificultad) { //ok
		String agrupamientoPedido = "SinAgrupamiento";
		Array<Categorias> categorias = new Array<Categorias>();
		categorias.add(categoria);
		int [] omitirArray = {omitir};
		return rsGet(agrupamientoPedido, categorias, omitirArray, dificultad);
	}
	
	
	
	
	
	
	public static class Agrupamientos {
		public String nombre;
		public Array<Integer> ids = new Array<Integer>();
	}
}
