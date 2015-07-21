# -*- coding: utf-8 -*-
from __future__ import division  #Para que las divisiones den bien!
"""
Created on Thu May 14 13:30:22 2015

@author: Ioni
"""

import numpy as np
import wave
import os
import glob
from PIL import Image as im

class Conf:
    Sample_rate = 44100 # Hz
    Canales = 2 
    Nombre_Predeterminado = 'test3.wav'
    Permisos = 'wb'
    Bits = 2
    Amplitud_Maxima = int (2**(8*Bits)/2) - 1
    Duracion_Total = 5 #segundos
    Duracion_Pixel = 1/20 # En segundos    (nota, para valores muy chicos que impliquen menos de un periodo hace lio)
    Volumen = 1 # Puede ir de 0 a 1
    Balance = 0 # Puede ir de -1 a 1
    Frecuencia_Maxima = 1000 #hz
    Frecuencia_Minima = 200 #hz
    #DirImagenes = "E:\Ioni\Dropbox\Programacion\Contornos\android\assets\experimentalsource\1"
    DirImagenes = os.getcwd()+'\\'
    ExtensionImagenes = '*.png'


def procesar_matriz (imagen):
    for numero_columna in range(imagen.shape[1]):
        columna = imagen[:,numero_columna]
        if not np.sum(columna) == 0:
            columna = columna / np.sum(columna)
        imagen[:,numero_columna] = columna
    return imagen

def crear_modulador (espectro,n_altura):
    moduladora = np.zeros(espectro.shape[1]*Conf.Duracion_Pixel*Conf.Sample_rate*2) # Crea un array vacio (el * 2 es porque ya estan intercalados los dos canales)
    sub_tramo  = np.ones(Conf.Duracion_Pixel*Conf.Sample_rate*2) # Crea un array vacio (el * 2 es porque ya estan intercalados los dos canales)
    for n_fila in range(espectro.shape[1]):
        sub_tramo = np.ones(Conf.Duracion_Pixel*Conf.Sample_rate*2) # Crea un array vacio (el * 2 es porque ya estan intercalados los dos canales)
        sub_tramo = sub_tramo * espectro[n_altura,n_fila]
        moduladora [n_fila*len(sub_tramo):(n_fila+1)*len(sub_tramo)] = sub_tramo
    return moduladora
        



"""

    Seccion que carga y procesa las imagenes

"""

imagenes_files = glob.glob(Conf.DirImagenes+Conf.ExtensionImagenes)

for imagen_file in imagenes_files:
    imagen = im.open(imagen_file) # Carga la imagen
    imagenGray = imagen.convert('L') # La convierte en escala de grises
    matriz=np.array(imagenGray) # La pasa a formato matriz
    matrizInvertida = (255 - matriz) # Invierte el brillo
            
    imagenMat=matrizInvertida


    imagen = imagenMat
    
    
    #imagen = np.array([[1.0,0,1],[0,1,0],[0,0,1],[0,0,1]]) #Cargo una imagen simple directo creando la matriz
    
    
    
    
    
    # Preparamos los datos
    imagen = imagen.astype(float)
    espectro = procesar_matriz(imagen)
    duracion = Conf.Duracion_Pixel*espectro.shape[1]
    numero_samples = int(duracion*Conf.Sample_rate)
    vol_ch1 = Conf.Volumen*(0.5+Conf.Balance)
    vol_ch2 = Conf.Volumen*(0.5-Conf.Balance)
    
    #Crea la escala sonora
    escalaSonora = range (espectro.shape[0]) #Crea una escala creciente en la linea vertical
    escalaSonora = np.array(escalaSonora) * (Conf.Frecuencia_Maxima-Conf.Frecuencia_Minima)/espectro.shape[0] # La reescala en el rango correcto
    escalaSonora = escalaSonora + Conf.Frecuencia_Minima # Le hace un offset para que empiece en el valor correcto
    escalaSonora = escalaSonora[::-1] # La invierte para que esten los agudos arriba
    
    senal_total = np.zeros(espectro.shape[1]*Conf.Duracion_Pixel*Conf.Sample_rate*2) # Crea un array vacio (el * 2 es porque ya estan intercalados los dos canales)
    # Creamos la señal para cada frecuencia
    for n_altura in range (espectro.shape[0]):
    
        # Define parametros propios de la frecuencia que toca usar
        frecuencia = escalaSonora[n_altura] # Hz
        periodo_samples = Conf.Sample_rate / frecuencia
        omega_samples = np.pi * 2 / periodo_samples
    
        # Crea el canal 1 (izquierdo), es facil porque se repite
        xaxis = np.arange(int(periodo_samples),dtype = np.float) * omega_samples # Esto crea un array q va de 0 a 2pi con la cantidad de samples que corresponda segun la frecuencia y el framerate
        ydata_ch1 = vol_ch1*Conf.Amplitud_Maxima*np.sin(xaxis) # Crea un periodo
        senal_ch1 = np.resize(ydata_ch1, (numero_samples,)) # Repite la funcion de 1 periodo las veces necesarias hasta completar el largo de la señal
    
        # Crea el canal dos, como la fase varia lentamente hay que crearlo de una sola vez
        if Conf.Canales == 2:
            xaxis = np.resize (xaxis,(numero_samples)) # extiende la base de x repitiendo los valores hasta completar
            desfazaje = (np.arange(len(xaxis))/len(xaxis) - 0.5 ) * np.pi
            xaxis = xaxis + desfazaje
            ydata_ch2 = vol_ch2*Conf.Amplitud_Maxima*np.sin(xaxis) 
            senal_ch2 = ydata_ch2    
    
        # Junta los dos canales        
        if Conf.Canales == 1:
            senal = senal_ch1
        if Conf.Canales == 2:
            senal = np.array([senal_ch1,senal_ch2]) # Los acomoda en una matriz
            senal = np.reshape (senal, -1, order='F') # Reordena la matriz para que se alternen los valores
    
        # Modulamos el canal en funcion de la intensidad del espectro
        moduladora = crear_modulador(espectro,n_altura)
        senal = senal * moduladora
        senal_total += senal
    
    ssignal = ''
    for i in range(len(senal_total)):
       ssignal += wave.struct.pack('h',senal_total[i]) # transform to binary (el 'h' indica que son enteros de 2 bits con signo)
    
    
    Archivo = wave.open(imagen_file[:-4]+".wav", Conf.Permisos) ## Hay que ver que le saque la extension aca!
    Archivo.setparams((Conf.Canales, Conf.Bits, Conf.Sample_rate, numero_samples*Conf.Canales, 'NONE', 'noncompressed'))
    Archivo.writeframes(ssignal)
    Archivo.close()
    
    print (imagen_file)
    
