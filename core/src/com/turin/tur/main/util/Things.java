package com.turin.tur.main.util;

/*
 * Esto esta a medio hacer!
 */

public class Things {

	private static final String TAG = Things.class.getName();
	
	public class Fecha {
		long millis;
		long seconds;
		long startSec = 1420070400; // Fecha a partir de la que se calcula todo
		String startString = "1/1/2015"; // String de la fecha a partir de la
											// que se calcula todo
		String dia;
		String mes;
		String ano;
		String horas;
		String horasGTMmas3;
		String minutos;
		String segundos;
		String segundosDecimal;
		String fecha;
		String hora;
		String horaGTMmas3;
		String comment;
		
		public Fecha(long timeMillis){
			this.millis=timeMillis;
			this.seconds = this.millis/1000;
			if (timeMillis < startSec) {
				this.comment = "Fecha anterior al 1/1/2015";
			} else if (timeMillis > startSec + ConstantsDates.secLastDate ) { 
				long temp = this.seconds;
			} else {
				
			}
			
		}
	}

	public static class ConstantsDates {
		public static final long startSec = 1420070400; // Fecha a partir de la que se calcula todo
		public static final String startString = "1/1/2015"; 
		public static final long secLastDate = 0;
		public static final long secDay = 86400;
		public static final long secHour = 3600;
		public static final long secMin = 60;
		public static final long secEnero = 31 * secDay;
		public static final long secFebreroNB = 28 * secDay;
		public static final long secFebreroB = 29 * secDay;
		public static final long secMarzo = 31 * secDay;
		public static final long secAbril = 30 * secDay;
		public static final long secMayo = 31 * secDay;
		public static final long secJunio = 30 * secDay;
		public static final long secJulio = 31 * secDay;
		public static final long secAgosto = 31 * secDay;
		public static final long secSeptiembre = 30 * secDay;
		public static final long secOctubre = 31 * secDay;
		public static final long secNoviembre = 30 * secDay;
		public static final long secDiciembre = 31 * secDay;
		public static final long secAnoNB = secEnero + secFebreroNB + secMarzo + secAbril + secMayo
				+ secJunio + secJulio + secAgosto + secSeptiembre + secOctubre
				+ secNoviembre + secDiciembre;
		public static final long secAnoB = secEnero + secFebreroNB + secMarzo + secAbril + secMayo
				+ secJunio + secJulio + secAgosto + secSeptiembre + secOctubre
				+ secNoviembre + secDiciembre;
		public static final long sec2015=secAnoNB;
		public static final long sec2016=secAnoB;
		public static final long sec2017=secAnoNB;
		public static final long sec2018=secAnoNB;
	}

}
