package cz.incad.nkp.inprove.permonikapi.originentities.specimen;

public interface SpecimenDefinition {
    String SPECIMEN_CORE_NAME = "exemplar";
    String ID_FIELD = "id";
    String ID_ISSUE_FIELD = "id_issue";
    String ID_META_TITLE_FIELD = "id_titul";
    String BAR_CODE_FIELD = "carovy_kod";
    String NUM_EXISTS_FIELD = "numExists";
    String NUM_MISSING_FIELD = "missing_number";
    String SIGNATURE_FIELD = "signatura";
    String OWNER_FIELD = "vlastnik";
    String STATES_FIELD = "stav";
    String STATE_FIELD = "state";
    String STATE_DESCRIPTION_FIELD = "stav_popis";
    String PAGES_FIELD = "pages";
    String NOTE_FIELD = "poznamka";
    // Probably same field as META_TITLE_NAME_FIELD
    String NAME_FIELD = "nazev";
    String SUB_NAME_FIELD = "podnazev";
    String PUBLICATION_FIELD = "vydani";
    String MUTATION_FIELD = "mutace";
    String PUBLICATION_MARK_FIELD = "znak_oznaceni_vydani";
    String PUBLICATION_DATE_FIELD = "datum_vydani";
    String PUBLICATION_DAY_FIELD = "datum_vydani_den";
    String PERIODICITY_FIELD = "periodicita";
    String NUMBER_FIELD = "cislo";
    String META_TITLE_NAME_FIELD = "meta_nazev";
    String PAGES_COUNT_FIELD = "pocet_stran";
    String IS_ATTACHMENT_FIELD = "isPriloha";
}
