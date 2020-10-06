package pl.coderslab.cultureBuddies.googleapis;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LettersUtilsTest {

    @Test
    void replacingAllAccents() {
        final String accents = "È,É,Ê,Ë,Û,Ù,Ï,Î,À,Â,Ô,è,é,ê,ë,û,ù,ï,î,à,â,ô,Ç,ç,Ã,ã,Õ,õ";
        final String expected = "E,E,E,E,U,U,I,I,A,A,O,e,e,e,e,u,u,i,i,a,a,o,C,c,A,a,O,o";

        final String accents2 = "çÇáéíóúýÁÉÍÓÚÝàèìòùÀÈÌÒÙãõñäëïöüÿÄËÏÖÜÃÕÑâêîôûÂÊÎÔÛ";
        final String expected2 = "cCaeiouyAEIOUYaeiouAEIOUaonaeiouyAEIOUAONaeiouAEIOU";

        final String accents3 = "Gisele Bündchen da Conceição e Silva foi batizada assim em homenagem à sua conterrânea de Horizontina, RS.";
        final String expected3 = "Gisele Bundchen da Conceicao e Silva foi batizada assim em homenagem a sua conterranea de Horizontina, RS.";

        final String accents4 = "/Users/rponte/arquivos-portalfcm/Eletron/Atualização_Diária-1.23.40.exe";
        final String expected4 = "/Users/rponte/arquivos-portalfcm/Eletron/Atualizacao_Diaria-1.23.40.exe";

        final String accents5 = "ąęóśćńżź";
        final String expected5 = "aeoscnzz";


        assertEquals(expected, LettersUtils.replaceSpecialLetters(accents));
        assertEquals(expected2, LettersUtils.replaceSpecialLetters(accents2));
        assertEquals(expected3, LettersUtils.replaceSpecialLetters(accents3));
        assertEquals(expected4, LettersUtils.replaceSpecialLetters(accents4));
        assertEquals(expected5, LettersUtils.replaceSpecialLetters(accents5));
    }

}