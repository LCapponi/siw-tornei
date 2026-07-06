// src/types/index.ts

export type Torneo = {
  id: number;
  nome: string;
  anno: number;
  descrizione: string;
};

// Rispecchia i getter di ClassificaRow lato Java (Jackson usa i nomi dei getter)
export type ClassificaRow = {
  nome: string;
  partiteGiocate: number;
  vittorie: number;
  pareggi: number;
  sconfitte: number;
  golFatti: number;
  golSubiti: number;
  differenzaReti: number;
  punti: number;
};