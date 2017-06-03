/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Bubble;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Pedro
 */
public class BubbleSize
{
    private AffineTransform affinetransform = new AffineTransform();     
    private FontRenderContext frc = new FontRenderContext(affinetransform,true,true);     
    private Font font = new java.awt.Font("Segoe UI Light", 0, 14);
    private int maxwidth = 350;
    private int singleLineHeight = (int)(font.getStringBounds("A", frc).getHeight());
    private int heightMin = 21;
    public Dimension getDimenssao(String nome, String texto)
    {
        int height = getHeight(texto);
        int width = (getWidth(texto) < getWidth(nome) ? getWidth(nome) : getWidth(texto));
        return new Dimension(width+20, height+10);
    }
    private int getWidth(String texto)
    {
        if(texto.contains("@<newLine/>"))
        {
            List<String> lista = new ArrayList<>(Arrays.asList(texto.split("@<newLine/>")));
            int maior = maior(lista);
            return maior >= maxwidth ? maxwidth : maior;
        }
        else
        {
            int w = (int)(font.getStringBounds(texto, frc).getWidth());
            return (w >= maxwidth) ? maxwidth : w;
        }
    }
    private int getHeight(String texto)
    {
        if(texto.contains("@<newLine/>"))
        {
            List<String> lista = new ArrayList<>(Arrays.asList(texto.split("@<newLine/>")));
            int linhasTotal = lista.size();
            for(String a : lista)
            {
                linhasTotal += getLinhas(a) - 1;
            }
            if(linhasTotal >= 3)
            {
                heightMin+= linhasTotal*2;
            }
            return heightMin + linhasTotal*singleLineHeight;
        }
        else
        {
            int linhas = getLinhas(texto);
            if(linhas >= 3)
            {
                heightMin+= linhas*2;
            }
            return heightMin + linhas*singleLineHeight;
        }
    }
    private int getLinhas(String texto)
    {
        int linhas = 1;
        int width = (int)(font.getStringBounds(texto, frc).getWidth());
        if(width > maxwidth)
        {
            linhas = ( width + (maxwidth - 1) ) / maxwidth;
        }
        return linhas;
    }
    private int maior(List<String> lista)
    {
        int maior = 0;
        for(String text : lista)
        {
            int textwidth  = (int)(font.getStringBounds(text, frc).getWidth());
            if(textwidth > maior)
            {
                maior = textwidth;
            }
        }
        return maior;
    }
}
