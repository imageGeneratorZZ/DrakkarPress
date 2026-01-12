#!/usr/bin/env python3
"""
Backend Python para DrakkarPress Desktop App
Servidor Flask que expone endpoints para generación de contenido con IA
"""

from flask import Flask, request, jsonify, Response
from flask_cors import CORS
import sys
import os
import json
import logging
from pathlib import Path
from typing import Dict, Any, Optional
import time
from dataclasses import asdict

# Agregar directorio raíz al path para imports
ROOT_DIR = Path(__file__).resolve().parent.parent
sys.path.insert(0, str(ROOT_DIR))

# Importar generadores existentes
try:
    from plantilla_expansion import DraftConfig, parse_outline, generate, Chapter
    PLANTILLA_AVAILABLE = True
except ImportError as e:
    print(f"⚠️  plantilla_expansion no disponible: {e}")
    PLANTILLA_AVAILABLE = False

try:
    from GENERADOR_MAESTRO_GROQ import gen_chap, all_ctx
    GROQ_AVAILABLE = True
except Exception as e:
    print(f"⚠️  GENERADOR_MAESTRO_GROQ no disponible: {e}")
    GROQ_AVAILABLE = False

# Importar OpenAI
try:
    from openai import OpenAI
    OPENAI_AVAILABLE = True
except ImportError as e:
    print(f"⚠️  OpenAI no disponible: {e}")
    OPENAI_AVAILABLE = False

# Configuración Flask
app = Flask(__name__)
CORS(app)  # Permitir CORS para comunicación con Electron

# Configuración logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

# Cache de contextos cargados
CONTEXTS_CACHE: Dict[str, Any] = {}
OUTLINE_CACHE: Dict[str, Any] = {}

# Cliente OpenAI
openai_client = None
if OPENAI_AVAILABLE:
    api_key = os.environ.get('OPENAI_API_KEY')
    if api_key:
        openai_client = OpenAI(api_key=api_key)
        logger.info("✅ OpenAI client inicializado")
    else:
        logger.warning("⚠️  OPENAI_API_KEY no configurada")

# Función helper para generar con OpenAI
def generate_with_openai(prompt: str, max_tokens: int = 2000) -> str:
    """Generar texto usando OpenAI GPT-4"""
    if not openai_client:
        raise Exception("OpenAI no está configurado. Define OPENAI_API_KEY")
    
    response = openai_client.chat.completions.create(
        model="gpt-4-turbo-preview",
        messages=[
            {"role": "system", "content": "Eres un escritor profesional especializado en narrativa profunda y contenido de alta calidad."},
            {"role": "user", "content": prompt}
        ],
        max_tokens=max_tokens,
        temperature=0.85
    )
    
    return response.choices[0].message.content


# =============================================================================
# HEALTH CHECK
# =============================================================================

@app.route('/health', methods=['GET'])
def health_check():
    """Verificar estado del servidor"""
    return jsonify({
        'status': 'ok',
        'timestamp': time.time(),
        'generators': {
            'plantilla_expansion': PLANTILLA_AVAILABLE,
            'groq_maestro': GROQ_AVAILABLE,
            'openai': OPENAI_AVAILABLE and openai_client is not None
        }
    })


# =============================================================================
# GENERADORES BÁSICOS
# =============================================================================

@app.route('/api/ai/idea', methods=['POST'])
def generate_idea():
    """Generar idea de libro basada en prompt"""
    try:
        data = request.get_json()
        prompt = data.get('prompt', '')
        genre = data.get('genre', 'General')
        
        logger.info(f"Generando idea - Género: {genre}, Prompt: {prompt[:50]}...")
        
        if openai_client:
            # Usar OpenAI para generar
            full_prompt = f"""Genera una idea completa de libro para el género {genre}.

Prompt del usuario: {prompt}

Incluye:
1. Concepto principal
2. Temas centrales (3-4 temas)
3. Gancho comercial
4. Arco narrativo de 3 actos
5. Target/audiencia

Formato profesional, estilo persuasivo."""
            
            result = generate_with_openai(full_prompt, max_tokens=1000)
            return Response(f"📚 IDEA DE LIBRO GENERADA\n\n{result}", mimetype='text/plain')
        else:
            # Fallback placeholder
            result = f"""📚 IDEA DE LIBRO GENERADA\n\nGénero: {genre}\n\nConcepto Principal:\n{prompt if prompt else 'Una historia sobre redención, trauma y resistencia'}\n\nTemas Centrales:\n- Supervivencia y empoderamiento\n- Relaciones complejas y toxicidad\n- Búsqueda de identidad\n- Justicia y venganza\n\nGancho Comercial:\nUna narrativa cruda que explora las consecuencias del abuso y el poder transformador de la verdad.\n\nArco Narrativo Sugerido:\n- Acto 1: Establecimiento del conflicto central\n- Acto 2: Escalada de tensión y revelaciones\n- Acto 3: Confrontación y resolución\n\nTarget: Adultos (18+) interesados en ficción psicológica y romance oscuro\n\n⚠️  MODO DEMO - Configura OPENAI_API_KEY para generación real
"""
            return Response(result, mimetype='text/plain')
        
    except Exception as e:
        logger.error(f"Error generando idea: {e}")
        return jsonify({'error': str(e)}), 500


@app.route('/api/ai/character', methods=['POST'])
def generate_character():
    """Generar perfil de personaje"""
    try:
        data = request.get_json()
        prompt = data.get('prompt', '')
        
        logger.info(f"Generando personaje: {prompt[:50]}...")
        
        if openai_client:
            full_prompt = f"""Crea un perfil detallado de personaje literario basado en: {prompt}

Incluye:
- Nombre completo
- Características físicas distintivas
- Personalidad (rasgos, motivaciones, miedos)
- Historia personal (familia, eventos formativos, trauma)
- Relaciones importantes
- Arco de transformación

Estilo profesional, profundidad psicológica."""
            
            result = generate_with_openai(full_prompt, max_tokens=1200)
            return Response(f"👤 PERFIL DE PERSONAJE\n\n{result}", mimetype='text/plain')
        else:
            result = f"""👤 PERFIL DE PERSONAJE\n\nNombre: [Generado basado en contexto]\n\nCaracterísticas Físicas:\n- Descripción general basada en prompt\n\nPersonalidad:\n- Rasgos dominantes\n- Motivaciones internas\n- Miedos y conflictos\n\nHistoria:\n- Contexto familiar\n- Eventos formativos\n- Trauma o conflicto central\n\nRelaciones:\n- Vínculos importantes\n- Dinámicas de poder\n\nArco de Transformación:\n- Inicio: {prompt[:100] if prompt else 'Estado inicial del personaje'}\n- Final: Evolución propuesta\n\n⚠️  MODO DEMO - Configura OPENAI_API_KEY para generación real
"""
            return Response(result, mimetype='text/plain')
        
    except Exception as e:
        logger.error(f"Error generando personaje: {e}")
        return jsonify({'error': str(e)}), 500


@app.route('/api/ai/synopsis', methods=['POST'])
def generate_synopsis():
    """Generar sinopsis de libro"""
    try:
        data = request.get_json()
        prompt = data.get('prompt', '')
        length = data.get('length', 'medium')  # short, medium, long
        
        logger.info(f"Generando sinopsis {length}: {prompt[:50]}...")
        
        words_target = {'short': 150, 'medium': 300, 'long': 500}[length]
        
        result = f"""📝 SINOPSIS ({words_target} palabras)

{prompt if prompt else 'Una historia sobre...'} 

[Párrafo 1: Establecimiento del mundo y protagonista]
Una mujer enfrenta las consecuencias de decisiones pasadas mientras lucha por proteger su futuro.

[Párrafo 2: Conflicto central]
Cuando secretos enterrados comienzan a emerger, debe elegir entre silencio cómplice y verdad devastadora.

[Párrafo 3: Stakes y gancho]
En un mundo donde el poder corrompe y el amor lastima, ¿es posible la redención?

Género: Drama psicológico / Romance oscuro
Target: 18+
"""
        
        return Response(result, mimetype='text/plain')
        
    except Exception as e:
        logger.error(f"Error generando sinopsis: {e}")
        return jsonify({'error': str(e)}), 500


# =============================================================================
# GENERADOR DE CAPÍTULOS (plantilla_expansion.py)
# =============================================================================

@app.route('/api/ai/chapter/expand', methods=['POST'])
def expand_chapter():
    """Expandir capítulo usando plantilla_expansion.py"""
    try:
        if not PLANTILLA_AVAILABLE:
            return jsonify({'error': 'plantilla_expansion no disponible'}), 503
            
        data = request.get_json()
        
        # Configuración del capítulo
        chapter_num = data.get('chapter_num', 1)
        tomo_num = data.get('tomo_num', 1)
        outline_text = data.get('outline', '')
        
        # Configuración de generación
        config = DraftConfig(
            pov=data.get('pov', 'tercera'),
            tone=data.get('tone', 'sobrio-sensual'),
            target_words=data.get('target_words', 2000),
            erotica_style=data.get('erotica_style', 'explicito-sutil'),
            ideology_treatment='contextual-responsable',
            include_references=data.get('include_references', True)
        )
        
        logger.info(f"Expandiendo capítulo {chapter_num} del tomo {tomo_num}")
        logger.info(f"Configuración: {config.target_words} palabras, tono {config.tone}")
        
        # Parsear outline si se proporciona
        if outline_text:
            chapters = parse_outline(outline_text)
        else:
            # Usar outline por defecto del tomo correspondiente
            outline_file = ROOT_DIR / f"TOMO{tomo_num}_ESTRUCTURA_DETALLADA.md"
            if outline_file.exists():
                with open(outline_file, 'r', encoding='utf-8') as f:
                    chapters = parse_outline(f.read())
            else:
                return jsonify({'error': f'No se encontró outline para tomo {tomo_num}'}), 404
        
        # Buscar el capítulo específico
        target_chapter = None
        for ch in chapters:
            if ch.number == chapter_num:
                target_chapter = ch
                break
        
        if not target_chapter:
            return jsonify({'error': f'Capítulo {chapter_num} no encontrado en outline'}), 404
        
        # Generar contenido
        logger.info(f"Generando: {target_chapter.title}")
        draft_text = generate(target_chapter, config, chapters)
        
        return Response(draft_text, mimetype='text/plain')
        
    except Exception as e:
        logger.error(f"Error expandiendo capítulo: {e}")
        return jsonify({'error': str(e)}), 500


# =============================================================================
# GENERADOR MAESTRO (GENERADOR_MAESTRO_GROQ.py)
# =============================================================================

@app.route('/api/ai/book/generate-maestro', methods=['POST'])
def generate_maestro():
    """Generar capítulos usando GENERADOR_MAESTRO_GROQ.py"""
    try:
        if not GROQ_AVAILABLE:
            return jsonify({'error': 'GENERADOR_MAESTRO_GROQ no disponible'}), 503
            
        data = request.get_json()
        chapter_num = data.get('chapter_num', 1)
        prev_context = data.get('prev_context', None)
        
        logger.info(f"Generando con Groq Maestro - Capítulo {chapter_num}")
        
        # Generar con el generador maestro
        result = gen_chap(chapter_num, prev_context)
        
        if result is None:
            return jsonify({'error': f'No hay contexto para capítulo {chapter_num}'}), 404
        
        return Response(result, mimetype='text/plain')
        
    except Exception as e:
        logger.error(f"Error con generador maestro: {e}")
        return jsonify({'error': str(e)}), 500


# =============================================================================
# ENDPOINTS ADICIONALES
# =============================================================================

@app.route('/api/ai/dialogue', methods=['POST'])
def generate_dialogue():
    """Generar diálogo entre personajes"""
    try:
        data = request.get_json()
        prompt = data.get('prompt', '')
        characters = data.get('characters', [])
        tone = data.get('tone', 'natural')
        
        logger.info(f"Generando diálogo - Personajes: {characters}, Tono: {tone}")
        
        result = f"""💬 DIÁLOGO GENERADO

Personajes: {', '.join(characters) if characters else 'A definir'}
Tono: {tone}

---

{prompt if prompt else '[Escena de diálogo]'}

[DIÁLOGO PLACEHOLDER - Integrar con Groq API]

---

Notas:
- Mantener voces distintivas para cada personaje
- Subtexto y tensión emocional
- Balance entre exposición y acción
"""
        
        return Response(result, mimetype='text/plain')
        
    except Exception as e:
        logger.error(f"Error generando diálogo: {e}")
        return jsonify({'error': str(e)}), 500


@app.route('/api/ai/titles', methods=['POST'])
def generate_titles():
    """Sugerir títulos para libro"""
    try:
        data = request.get_json()
        prompt = data.get('prompt', '')
        count = data.get('count', 10)
        
        logger.info(f"Generando {count} títulos")
        
        # TODO: Implementar con Groq API
        titles = [
            "Cenizas de Silencio",
            "La Voz que Arde",
            "Sombras de Verdad",
            "Romper el Cristal",
            "Ecos de Resistencia",
            "Entre Líneas de Fuego",
            "El Peso de las Palabras",
            "Raíces Rotas",
            "Cuando el Silencio Grita",
            "La Última Confesión"
        ]
        
        result = "🏷️ TÍTULOS SUGERIDOS\n\n" + "\n".join([f"{i+1}. {t}" for i, t in enumerate(titles[:count])])
        
        return Response(result, mimetype='text/plain')
        
    except Exception as e:
        logger.error(f"Error generando títulos: {e}")
        return jsonify({'error': str(e)}), 500


# =============================================================================
# MAIN
# =============================================================================

if __name__ == '__main__':
    port = int(os.environ.get('BACKEND_PORT', 5000))
    logger.info(f"🚀 Backend Python iniciando en puerto {port}")
    logger.info(f"📁 Directorio raíz: {ROOT_DIR}")
    logger.info(f"✅ plantilla_expansion: {'Disponible' if PLANTILLA_AVAILABLE else 'No disponible'}")
    logger.info(f"✅ GENERADOR_MAESTRO_GROQ: {'Disponible' if GROQ_AVAILABLE else 'No disponible'}")
    
    app.run(
        host='127.0.0.1',
        port=port,
        debug=True,
        threaded=True
    )
