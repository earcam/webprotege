package edu.stanford.bmir.protege.web.client.lang;

import com.google.common.collect.ImmutableList;
import com.google.gwt.user.client.ui.IsWidget;
import edu.stanford.bmir.protege.web.shared.entity.OWLAnnotationPropertyData;
import edu.stanford.bmir.protege.web.shared.shortform.DictionaryLanguageData;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 31 Jul 2018
 */
public interface DisplayLanguageEditorView extends IsWidget {

    void setPrimaryDisplayNameLanguages(@Nonnull ImmutableList<DictionaryLanguageData> languages);

    @Nonnull
    ImmutableList<DictionaryLanguageData> getPrimaryDisplayNameLanguages();

    void setSecondaryDisplayNameLanguages(@Nonnull ImmutableList<DictionaryLanguageData> languages);

    @Nonnull
    ImmutableList<DictionaryLanguageData> getSecondaryDisplayNameLanguages();


    void setChangeHandler(@Nonnull ChangeHandler changeHandler);

    interface ChangeHandler {

        void handleDisplayLanguageChanged();
    }
}
