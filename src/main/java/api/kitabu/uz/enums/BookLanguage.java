package api.kitabu.uz.enums;

public enum BookLanguage {
    EN("Ingliz", "English", "Английский"),
    RU("Rus", "Russion", "Русский"),
    LATIN("Lotin", "Latin", "Латинский"),
    KIRILL("Kirill", "Kirill", "Кирилл");

    private String nameUz;
    private String nameRu;
    private String nameEn;


    BookLanguage(String nameUz, String nameEn, String nameRu) {
        this.nameUz = nameUz;
        this.nameEn = nameEn;
        this.nameRu = nameRu;
    }

    public String getNameUz() {
        return nameUz;
    }

    public void setNameUz(String nameUz) {
        this.nameUz = nameUz;
    }

    public String getNameRu() {
        return nameRu;
    }

    public void setNameRu(String nameRu) {
        this.nameRu = nameRu;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }
}
