// src/App.tsx
import { useState, useEffect, useMemo } from 'react';
import type { CSSProperties } from 'react';
import type { Torneo, ClassificaRow } from './types';
import { getTornei, getClassifica } from './services/torneoService';

type SortKey = 'anno-desc' | 'anno-asc' | 'nome';

// ---------- Tabella classifica (caricata su richiesta quando si apre il torneo) ----------
function ClassificaTabella({ torneoId }: { torneoId: number }) {
  const [righe, setRighe] = useState<ClassificaRow[] | null>(null);
  const [loading, setLoading] = useState(true);  // parte già in caricamento: niente setState sincrono nell'effetto
  const [errore, setErrore] = useState<string | null>(null);

  useEffect(() => {
    let attivo = true; // evita setState dopo unmount
    getClassifica(torneoId)
      .then((data) => { if (attivo) setRighe(data); })
      .catch(() => { if (attivo) setErrore('Impossibile caricare la classifica.'); })
      .finally(() => { if (attivo) setLoading(false); });
    return () => { attivo = false; };
  }, [torneoId]);

  if (loading) return <p style={{ opacity: 0.7, margin: '10px 0 0' }}>Caricamento classifica…</p>;
  if (errore) return <p style={{ color: 'crimson', margin: '10px 0 0' }}>{errore}</p>;
  if (!righe || righe.length === 0)
    return <p style={{ opacity: 0.7, margin: '10px 0 0' }}>Nessuna partita giocata: classifica vuota.</p>;

  return (
    <table style={tabella}>
      <thead>
        <tr>
          <th style={thL}>#</th>
          <th style={thL}>Squadra</th>
          <th style={thC} title="Partite giocate">PG</th>
          <th style={thC} title="Vittorie">V</th>
          <th style={thC} title="Pareggi">N</th>
          <th style={thC} title="Sconfitte">P</th>
          <th style={thC} title="Gol fatti">GF</th>
          <th style={thC} title="Gol subiti">GS</th>
          <th style={thC} title="Differenza reti">DR</th>
          <th style={thC} title="Punti">Pt</th>
        </tr>
      </thead>
      <tbody>
        {righe.map((r, i) => (
          <tr key={r.nome}>
            <td style={tdL}>{i + 1}</td>
            <td style={tdL}>{r.nome}</td>
            <td style={tdC}>{r.partiteGiocate}</td>
            <td style={tdC}>{r.vittorie}</td>
            <td style={tdC}>{r.pareggi}</td>
            <td style={tdC}>{r.sconfitte}</td>
            <td style={tdC}>{r.golFatti}</td>
            <td style={tdC}>{r.golSubiti}</td>
            <td style={tdC}>{r.differenzaReti}</td>
            <td style={{ ...tdC, fontWeight: 'bold' }}>{r.punti}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}

// ---------------------------------- App ----------------------------------
function App() {
  const [tornei, setTornei] = useState<Torneo[]>([]);
  const [loading, setLoading] = useState(true);
  const [errore, setErrore] = useState<string | null>(null);

  const [query, setQuery] = useState('');
  const [sort, setSort] = useState<SortKey>('anno-desc');
  const [apertoId, setApertoId] = useState<number | null>(null); // torneo espanso

  useEffect(() => {
    getTornei()
      .then(setTornei)
      .catch(() => setErrore('Impossibile caricare i tornei. Verifica che il server sia attivo.'))
      .finally(() => setLoading(false));
  }, []);

  // Ricerca + ordinamento lato client
  const torneiVisibili = useMemo(() => {
    const q = query.trim().toLowerCase();
    const filtrati = tornei.filter((t) => t.nome.toLowerCase().includes(q));
    return [...filtrati].sort((a, b) => {
      if (sort === 'nome') return a.nome.localeCompare(b.nome);
      if (sort === 'anno-asc') return a.anno - b.anno;
      return b.anno - a.anno; // anno-desc (default)
    });
  }, [tornei, query, sort]);

  const toggle = (id: number) => setApertoId((prec) => (prec === id ? null : id));

  return (
    <div style={{ padding: '20px', fontFamily: 'sans-serif', maxWidth: 900, margin: '0 auto' }}>
      <h1 style={{ textAlign: 'center' }}>🏆 Lista Tornei Amatoriali</h1>

      {/* Barra ricerca + ordinamento */}
      {!loading && !errore && tornei.length > 0 && (
        <div style={{ display: 'flex', gap: 10, flexWrap: 'wrap', margin: '20px 0' }}>
          <input
            type="text"
            placeholder="Cerca per nome…"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            style={{ flex: 1, minWidth: 180, padding: '8px 12px', borderRadius: 8, border: '1px solid #888', background: 'transparent', color: 'inherit' }}
          />
          <select
            value={sort}
            onChange={(e) => setSort(e.target.value as SortKey)}
            style={{ padding: '8px 12px', borderRadius: 8, border: '1px solid #888', background: 'transparent', color: 'inherit' }}
          >
            <option value="anno-desc">Anno (più recente)</option>
            <option value="anno-asc">Anno (meno recente)</option>
            <option value="nome">Nome (A→Z)</option>
          </select>
        </div>
      )}

      {/* Stati */}
      {loading && <p>Caricamento in corso…</p>}
      {errore && <p style={{ color: 'crimson' }}>{errore}</p>}
      {!loading && !errore && tornei.length === 0 && <p>Nessun torneo presente.</p>}
      {!loading && !errore && tornei.length > 0 && torneiVisibili.length === 0 && (
        <p style={{ opacity: 0.7 }}>Nessun torneo corrisponde a “{query}”.</p>
      )}

      {/* Lista */}
      <ul style={{ listStyleType: 'none', padding: 0 }}>
        {torneiVisibili.map((torneo) => {
          const aperto = apertoId === torneo.id;
          return (
            <li
              key={torneo.id}
              style={{ marginBottom: '15px', padding: '15px', border: '1px solid #888', borderRadius: '8px' }}
            >
              <div
                onClick={() => toggle(torneo.id)}
                style={{ cursor: 'pointer', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}
              >
                <div>
                  <h2 style={{ margin: '0 0 6px' }}>{torneo.nome}</h2>
                  <p style={{ margin: 0 }}><strong>Anno:</strong> {torneo.anno}</p>
                  <p style={{ margin: '4px 0 0' }}><strong>Descrizione:</strong> {torneo.descrizione}</p>
                </div>
                <span style={{ fontSize: '0.85rem', opacity: 0.8, whiteSpace: 'nowrap', marginLeft: 12 }}>
                  {aperto ? '▲ Nascondi classifica' : '▼ Classifica'}
                </span>
              </div>

              {aperto && <ClassificaTabella torneoId={torneo.id} />}
            </li>
          );
        })}
      </ul>
    </div>
  );
}

// ---------------------------------- stili tabella ----------------------------------
const tabella: CSSProperties = { width: '100%', borderCollapse: 'collapse', marginTop: 12, fontSize: '0.9rem' };
const thL: CSSProperties = { textAlign: 'left', padding: '6px 8px', borderBottom: '1px solid #888' };
const thC: CSSProperties = { textAlign: 'center', padding: '6px 8px', borderBottom: '1px solid #888' };
const tdL: CSSProperties = { textAlign: 'left', padding: '6px 8px', borderBottom: '1px solid #444' };
const tdC: CSSProperties = { textAlign: 'center', padding: '6px 8px', borderBottom: '1px solid #444' };

export default App;