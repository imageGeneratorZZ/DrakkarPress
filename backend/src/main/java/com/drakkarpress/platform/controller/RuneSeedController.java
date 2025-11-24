package com.drakkarpress.platform.controller;

import com.drakkarpress.platform.model.Rune;
import com.drakkarpress.platform.repository.RuneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/public")
public class RuneSeedController {

    @Autowired
    private RuneRepository runeRepository;

    @PostMapping("/seed-runes")
    public ResponseEntity<?> seedRunes() {
        try {
            // Verificar si ya existen runas
            long count = runeRepository.count();
            if (count > 0) {
                return ResponseEntity.ok("Runas ya existen en la base de datos. Total: " + count);
            }

            // Insertar runa por defecto con UUID fijo
            UUID fehuId = UUID.fromString("00000000-0000-0000-0000-000000000001");
            if (!runeRepository.existsById(fehuId)) {
                Rune fehu = new Rune();
                fehu.setId(fehuId);
                fehu.setSymbol("ᚠ");
                fehu.setName("Fehu");
                fehu.setMeaningEs("Abundancia, riqueza, prosperidad");
                fehu.setMeaningEn("Abundance, wealth, prosperity");
                fehu.setCategory("LEGACY_ABUNDANCE");
                fehu.setDescriptionEs("El ganado, símbolo de riqueza. La abundancia que llega al escritor, tanto material como espiritual.");
                fehu.setDescriptionEn("The cattle, symbol of wealth. The abundance that comes to the writer, both material and spiritual.");
                fehu.setDisplayOrder(1);
                fehu.setIsActive(true);
                fehu.setCreatedAt(LocalDateTime.now());
                fehu.setUpdatedAt(LocalDateTime.now());
                runeRepository.save(fehu);
            }

            // Insertar las 23 runas restantes
            String[][] runesData = {
                {"ᚲ", "Kenaz", "Creatividad, inspiración, luz interior", "Creativity, inspiration, inner light", "CREATIVITY_KNOWLEDGE", 
                 "La antorcha que ilumina el camino del escritor. Representa la chispa creativa y la inspiración divina que guía las palabras.", 
                 "The torch that illuminates the writer's path. Represents the creative spark and divine inspiration that guides words.", "2"},
                
                {"ᚨ", "Ansuz", "Sabiduría, comunicación, palabra divina", "Wisdom, communication, divine word", "CREATIVITY_KNOWLEDGE",
                 "La runa de Odín, dios de la sabiduría y la poesía. Simboliza el poder de la palabra escrita y la comunicación profunda.",
                 "The rune of Odin, god of wisdom and poetry. Symbolizes the power of the written word and deep communication.", "3"},
                
                {"ᛗ", "Mannaz", "Intelecto, el yo creador, humanidad", "Intellect, the creator self, humanity", "CREATIVITY_KNOWLEDGE",
                 "Representa al ser humano como creador. La conexión entre el escritor y sus lectores, la humanidad en las historias.",
                 "Represents the human being as creator. The connection between writer and readers, humanity in stories.", "4"},

                {"ᛊ", "Sowilo", "Éxito, victoria, poder solar", "Success, victory, solar power", "SUCCESS_ACHIEVEMENT",
                 "El sol que no se oculta. Representa el éxito alcanzado, la victoria del escritor que logra sus metas.",
                 "The sun that never sets. Represents success achieved, the victory of the writer who reaches their goals.", "5"},
                
                {"ᛃ", "Jera", "Cosecha, recompensa del esfuerzo, ciclos", "Harvest, reward of effort, cycles", "SUCCESS_ACHIEVEMENT",
                 "La cosecha tras la siembra. El fruto del trabajo constante del escritor, la recompensa merecida.",
                 "The harvest after sowing. The fruit of the writer's constant work, the deserved reward.", "6"},
                
                {"ᚹ", "Wunjo", "Alegría, perfección, éxito alcanzado", "Joy, perfection, success achieved", "SUCCESS_ACHIEVEMENT",
                 "La alegría del logro. Cuando la obra está completa y el escritor puede disfrutar de su creación.",
                 "The joy of achievement. When the work is complete and the writer can enjoy their creation.", "7"},
                
                {"ᛏ", "Tiwaz", "Honor, victoria justa, liderazgo", "Honor, just victory, leadership", "SUCCESS_ACHIEVEMENT",
                 "El guerrero honorable. Representa al escritor que lidera con integridad y alcanza victorias justas.",
                 "The honorable warrior. Represents the writer who leads with integrity and achieves just victories.", "8"},

                {"ᛒ", "Berkano", "Nuevo comienzo, crecimiento, renacimiento", "New beginning, growth, rebirth", "GROWTH_TRANSFORMATION",
                 "El abedul en primavera. Nuevos comienzos, el primer libro, el renacer del escritor en cada proyecto.",
                 "The birch in spring. New beginnings, the first book, the writer's rebirth in each project.", "9"},
                
                {"ᛞ", "Dagaz", "Despertar, transformación, iluminación", "Awakening, transformation, enlightenment", "GROWTH_TRANSFORMATION",
                 "El amanecer. La transformación del escritor, el momento de claridad donde todo cobra sentido.",
                 "The dawn. The writer's transformation, the moment of clarity where everything makes sense.", "10"},
                
                {"ᛁ", "Isa", "Concentración, enfoque, cristalización", "Concentration, focus, crystallization", "GROWTH_TRANSFORMATION",
                 "El hielo que preserva. La concentración necesaria para escribir, las ideas que se cristalizan en palabras.",
                 "The ice that preserves. The concentration needed to write, ideas that crystallize into words.", "11"},

                {"ᚦ", "Thurisaz", "Protección del trabajo, defensa", "Protection of work, defense", "PROTECTION_STRENGTH",
                 "El martillo de Thor. Protección contra el bloqueo del escritor, defensa de la obra creada.",
                 "Thor's hammer. Protection against writer's block, defense of the created work.", "12"},
                
                {"ᚢ", "Uruz", "Fuerza vital, resistencia, poder primitivo", "Vital force, endurance, primal power", "PROTECTION_STRENGTH",
                 "El uro salvaje. La fuerza bruta necesaria para perseverar, la resistencia del escritor comprometido.",
                 "The wild aurochs. The brute force needed to persevere, the endurance of the committed writer.", "13"},
                
                {"ᛉ", "Algiz", "Protección espiritual, conexión divina", "Spiritual protection, divine connection", "PROTECTION_STRENGTH",
                 "El alce con cornamenta. Protección espiritual durante el proceso creativo, conexión con lo divino.",
                 "The elk with antlers. Spiritual protection during the creative process, connection with the divine.", "14"},

                {"ᛚ", "Laguz", "Intuición, flujo creativo, lo oculto", "Intuition, creative flow, the hidden", "INTUITION_MYSTERY",
                 "El agua que fluye. La intuición del escritor, el flujo de palabras que emerge del subconsciente.",
                 "The flowing water. The writer's intuition, the flow of words that emerges from the subconscious.", "15"},
                
                {"ᛈ", "Perthro", "Misterio, destino, secretos revelados", "Mystery, destiny, secrets revealed", "INTUITION_MYSTERY",
                 "El cubilete de dados. Los misterios que el escritor revela, los giros del destino en las historias.",
                 "The dice cup. The mysteries the writer reveals, the twists of fate in stories.", "16"},

                {"ᛟ", "Othala", "Herencia, legado, patrimonio ancestral", "Heritage, legacy, ancestral heritage", "LEGACY_ABUNDANCE",
                 "La tierra ancestral. El legado que el escritor deja, las historias que perduran generaciones.",
                 "The ancestral land. The legacy the writer leaves, stories that endure for generations.", "17"},

                {"ᛖ", "Ehwaz", "Progreso, colaboración, asociación", "Progress, collaboration, partnership", "COLLABORATION_PROGRESS",
                 "El caballo y su jinete. La colaboración entre escritor y editor, el progreso conjunto.",
                 "The horse and its rider. The collaboration between writer and editor, joint progress.", "18"},
                
                {"ᚷ", "Gebo", "Intercambio, generosidad, reciprocidad", "Exchange, generosity, reciprocity", "COLLABORATION_PROGRESS",
                 "El regalo. El intercambio generoso entre escritores, la reciprocidad en la comunidad.",
                 "The gift. The generous exchange between writers, reciprocity in the community.", "19"},
                
                {"ᚱ", "Raidho", "Viaje, movimiento, camino del héroe", "Journey, movement, hero's path", "COLLABORATION_PROGRESS",
                 "El carro en movimiento. El viaje del escritor, el camino del héroe en cada historia.",
                 "The moving cart. The writer's journey, the hero's path in each story.", "20"},

                {"ᚺ", "Hagalaz", "Transformación, destrucción creativa", "Transformation, creative destruction", "GROWTH_TRANSFORMATION",
                 "El granizo. La transformación a través de la adversidad, la destrucción que permite la reconstrucción.",
                 "The hail. Transformation through adversity, destruction that allows reconstruction.", "21"},
                
                {"ᚾ", "Nauthiz", "Necesidad, destino, disciplina", "Need, destiny, discipline", "PROTECTION_STRENGTH",
                 "El fuego de la necesidad. La disciplina requerida para escribir, el destino que impulsa al escritor.",
                 "The fire of need. The discipline required to write, the destiny that drives the writer.", "22"},
                
                {"ᛇ", "Eihwaz", "Defensa, resistencia, perseverancia", "Defense, resistance, perseverance", "PROTECTION_STRENGTH",
                 "El tejo milenario. La resistencia del escritor ante las dificultades, la perseverancia inquebrantable.",
                 "The millennial yew. The writer's resistance to difficulties, unbreakable perseverance.", "23"},
                
                {"ᛜ", "Ingwaz", "Potencial, fertilidad, gestación", "Potential, fertility, gestation", "GROWTH_TRANSFORMATION",
                 "La semilla. El potencial latente, las ideas en gestación que pronto florecerán.",
                 "The seed. The latent potential, ideas in gestation that will soon flourish.", "24"}
            };

            for (String[] data : runesData) {
                if (runeRepository.findByName(data[1]).isEmpty()) {
                    Rune rune = new Rune();
                    rune.setSymbol(data[0]);
                    rune.setName(data[1]);
                    rune.setMeaningEs(data[2]);
                    rune.setMeaningEn(data[3]);
                    rune.setCategory(data[4]);
                    rune.setDescriptionEs(data[5]);
                    rune.setDescriptionEn(data[6]);
                    rune.setDisplayOrder(Integer.parseInt(data[7]));
                    rune.setIsActive(true);
                    rune.setCreatedAt(LocalDateTime.now());
                    rune.setUpdatedAt(LocalDateTime.now());
                    runeRepository.save(rune);
                }
            }

            return ResponseEntity.ok("✅ Seed completado: 24 runas insertadas exitosamente");
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
}
