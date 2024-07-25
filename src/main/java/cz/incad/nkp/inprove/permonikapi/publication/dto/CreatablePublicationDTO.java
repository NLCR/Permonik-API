package cz.incad.nkp.inprove.permonikapi.publication.dto;


// name as string
//    {
//        cs: "Ranní",
//        sk: "Ranné",
//        en: "Morning"
//    }
public record CreatablePublicationDTO(String name, Boolean isDefault, Boolean isAttachment,
                                      Boolean isPeriodicAttachment) {
}
