// src/services/torneoService.ts
import api from './api';
import type { Torneo, ClassificaRow } from '../types';

// Elenco tornei. L'errore non viene ingoiato: lo propaga al componente,
// che decide cosa mostrare (caricamento / errore / lista vuota).
export async function getTornei(): Promise<Torneo[]> {
  const { data } = await api.get<Torneo[]>('/rest/tornei');
  return data;
}

// Classifica di un singolo torneo.
export async function getClassifica(torneoId: number): Promise<ClassificaRow[]> {
  const { data } = await api.get<ClassificaRow[]>(`/rest/tornei/${torneoId}/classifica`);
  return data;
}