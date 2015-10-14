package com.turin.tur.main.util;

import java.math.BigInteger;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class Stadistics {
	

	/**
	 * 
	 * @param histograma
	 * 		Array de datos donde el indice indica el numero de opciones posibles y el valor la cantidad de trials con dicha cantidad de opciones
	 * @return
	 * 		Devuelve un array de datos donde el valor indica la probabilidad de encontrar ese numero de respuestas correctas asumiendo una sola opcion valida para cada trial y un criterio de respuesta totalmente aleatorio
	 */
	public static Float[] distribucion (int[] histograma){
		/*
		 *  Para calcular la distribucion de probabiliedades se usa el siguiente mecanismo:
		 *  
		 *  Primero se busca la probabilidad de tener n respuesta correctas en el subgrupo que comparten probabilidad.
		 *  Con esta operacion se obtienen i vectores (donde i es el iterador que se mueve en el numero de respuestas posibles por trial) con m+1 elementos (donde m es el numero de trial con i opciones).
		 *	El elemento n de cada uno de estos vectores representa la probabilidad de tener n respuestas correctas en el grupo i  
		 *	Luego, si se multiplica un elemento n_i de un vector por otro elemento n_i' de otro vector, se obtiene la probabilidad de tener en el conjunto de los trial con i e i' opciones, una cantidad n_i + n_i' de opciones correctas (para un camino posible).
		 *	Si en lugar de multiplicar dos elememtos se multiplica desde i=1 hasta i=I, se obtiene la probabilidad de un camino para obtener n_1+n_2+...+n_I respuestas correctas
		 *	Si luego se suman todas las probabilidades que corresponden a diferentes caminos pero que comparten la suma de los n_i, se obtiene la probabilidad total de obtener ese numero de opciones correctas.
		 *    	   
		 */
		
		// Generamos los vectores para cada i
		Array<Array<Float>> probabilidades = new Array<Array<Float>>();
		for (int i=1; i< histograma.length; i++) { // Itera sobre las opciones de i (numero de opciones entre las que elegir). El cero se saltea.
			int m = histograma[i];
			float i_f = i;
			float p = 1/i_f;
			Array<Float> probabilidades_i = new Array<Float>(); 
			for (int n=0; n<=m; n++) { // Itera entre las opciones correctas posibles para calcular su probabilidad
				/*
				 * Aca hay problemas porque los ordenes de magnitud de los numeros son feos!
				 */
				double factor1 = Math.pow(p, n);
				double factor2 = Math.pow(1-p,m-n);
				BigInteger factorial = fact(m).divide((fact(n).multiply(fact(m-n))));
				float probabilidad = (float) (factor1*factor2*factorial.doubleValue()); 
				probabilidades_i.add(probabilidad);
			}
			probabilidades.add(probabilidades_i);
		}
		
		/*
		 * Una vez obtenidos los resultados de probabilidad de cada grupo por separado debemos combinarlos.
		 * Como en principio hay que combinar un numero arbitrario de vectores, pero se pueden ir combinando de a dos
		 * la manera sencilla de hacerlo es primero combinar el primer y el segundo, y luego esa combinacion combinarla con el tercero, y asi sucesivamente hasta combinar todos
		 */
		
		//TODO SEGUIR verificando q ande bien!
		Float[] acumulado;
		if (probabilidades.size != 0) {
			acumulado = new Float[probabilidades.get(0).size];
			for (int j=0; j<probabilidades.get(0).size; j++) {
				acumulado[j]=probabilidades.get(0).get(j);
			}
		} else { // Pone probabilidad 1 de sacara un 0 
			acumulado = new Float[1];
			acumulado[0] = 1f;
		}
		for (int i=1; i<probabilidades.size; i++) {
			Float[] aCombinar = new Float[probabilidades.get(i).size];
			for (int j=0; j<probabilidades.get(i).size; j++) {
				aCombinar[j] = probabilidades.get(i).get(j);
			}
			Float[] combinado = new Float[acumulado.length-1 + aCombinar.length-1 + 1];
			for (int j=0; j<combinado.length; j++) {
				combinado[j]=0f;
			}
			
			for (int i_1 = 0; i_1 < aCombinar.length; i_1++) {
				for (int i_2 = 0; i_2 < acumulado.length; i_2++){

					combinado[i_1+i_2] = combinado[i_1+i_2] + aCombinar[i_1] * acumulado[i_2];
				}
			}
			acumulado = combinado;
		}
		return acumulado;
	}

	private static BigInteger fact(int n) {
		BigInteger resultado= BigInteger.ONE;
		for (int i=1; i<=n; i++)
			resultado = resultado.multiply(BigInteger.valueOf(i));
		return resultado;
	}
}
