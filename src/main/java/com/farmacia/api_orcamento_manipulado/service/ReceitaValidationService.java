package com.farmacia.api_orcamento_manipulado.service;

import com.farmacia.api_orcamento_manipulado.model.ItemOrcamento;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;
import java.util.Set;

@Service
public class ReceitaValidationService {

    private static final Set<String> MEDICAMENTOS_COMUNS = Set.of(
            "dipirona",
            "paracetamol",
            "ibuprofeno",
            "amoxicilina",
            "azitromicina",
            "ciprofloxacino",
            "diclofenaco",
            "nimesulida",
            "prednisona",
            "omeprazol",
            "pantoprazol",
            "lansoprazol",
            "esomeprazol",
            "losartana",
            "enalapril",
            "captopril",
            "hidroclorotiazida",
            "metformina",
            "glibenclamida",
            "gliclazida",
            "insulina",
            "sertralina",
            "fluoxetina",
            "citalopram",
            "nortriptilina",
            "loratadina",
            "cetirizina",
            "fexofenadina",
            "ranitidina",
            "famotidina",
            "salbutamol",
            "formoterol",
            "budesonida",
            "beclometasona",
            "dexametasona",
            "hidrocortisona",
            "clotrimazol",
            "fluconazol",
            "nistatina",
            "miconazol",
            "claritromicina",
            "rifampicina",
            "isoniazida",
            "atenolol",
            "metoprolol",
            "carvedilol",
            "atorvastatina",
            "sinvastatina",
            "rosuvastatina",
            "aciclovir",
            "valaciclovir",
            "amantadina",
            "clonazepam",
            "alprazolam",
            "diazepam",
            "lorazepam",
            "ezetimiba",
            "clopidogrel",
            "varfarina",
            "enoxaparina",
            "sulfato ferroso",
            "acido folico",
            "vitamina c",
            "vitamina d",
            "cetoprofeno",
            "bromoprida",
            "domperidona",
            "espironolactona",
            "glimepirida",
            "pioglitazona",
            "sitagliptina",
            "saxagliptina",
            "empagliflozina",
            "canagliflozina",
            "meloxicam",
            "tramadol",
            "clonidina",
            "metildopa",
            "fenobarbital",
            "carbonato de calcio",
            "sucralfato",
            "nitazoxanida",
            "albendazol",
            "ivermectina",
            "hidroxizina",
            "prometazina",
            "rivaroxabana",
            "dabigatrana",
            "pregabalina",
            "gabapentina",
            "amlodipino",
            "isosorbida",
            "sildenafil",
            "tadalafila",
            "fenitoina",
            "topiramato",
            "lamotrigina",
            "levotiroxina",
            "tiamina",
            "complexo b",
            "colecalciferol",
            "mecobalamina",
            "vitamina b12",
            "vitamina e",
            "vitamina k");

    public boolean isPrescricaoValida(List<ItemOrcamento> itens, String textoReceita) {
        // Se há itens extraídos, verificar se parecem medicamentos
        if (itens != null && !itens.isEmpty()) {
            for (ItemOrcamento item : itens) {
                if (item != null && item.getNome() != null) {
                    // Aceitar se é medicamento conhecido
                    if (containsMedicamentoConhecido(item.getNome())) {
                        return true;
                    }
                    // Ou se parece ser um medicamento (contém dosagem/unidade)
                    String itemNorm = normalize(item.getNome());
                    if (isMedicationLike(itemNorm)) {
                        return true;
                    }
                }
            }
        }

        // Se há texto (PDF/TXT)
        if (textoReceita != null && !textoReceita.isBlank()) {
            String normalizedTexto = normalize(textoReceita);
            if (containsReceitaKeywords(normalizedTexto)) {
                return true;
            }
            // Também aceitar se tem palavras que parecem medicamentos
            if (looksLikePrescription(normalizedTexto)) {
                return true;
            }
        }

        return false;
    }

    private boolean isMedicationLike(String texto) {
        // Padrão: texto com dosagem (mg, mcg, ui, ml) ou estrutura medicamentosa
        return (texto.contains("mg") || texto.contains("mcg") || texto.contains("ui") ||
                texto.contains("ml") || texto.contains("capsula") || texto.contains("comprimido") ||
                texto.contains("solucao") || texto.contains("suspensao"));
    }

    private boolean looksLikePrescription(String texto) {
        // Verificar se há padrões que indicam receita
        return (texto.contains("posologia") || texto.contains("tomar") ||
                texto.contains("beber") || texto.contains("aplicar") ||
                texto.contains("capsula") || texto.contains("comprimido") ||
                (texto.matches(".*\\d+\\s*(mg|mcg|ui|ml).*")));
    }

    private boolean containsMedicamentoConhecido(String texto) {
        String normalized = normalize(texto);
        for (String medicamento : MEDICAMENTOS_COMUNS) {
            if (normalized.contains(medicamento)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsReceitaKeywords(String texto) {
        return texto.contains("receita")
                || texto.contains("prescricao")
                || texto.contains("prescrição")
                || texto.contains("medicamento")
                || texto.contains("comprimido")
                || texto.contains("cápsula")
                || texto.contains("capsula")
                || texto.contains("mg")
                || texto.contains("ml")
                || texto.contains("uso oral")
                || texto.contains("uso tópico")
                || texto.contains("farmaceutico")
                || texto.contains("farmacêutico")
                || texto.contains("posologia")
                || texto.contains("sig")
                || texto.contains("dose")
                || texto.contains("aprazamento")
                || texto.contains("tarja")
                || texto.contains("prescrito");
    }

    private String normalize(String texto) {
        if (texto == null) {
            return "";
        }
        String normalized = Normalizer.normalize(texto, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("\\p{M}", "");
        return normalized.toLowerCase();
    }
}
