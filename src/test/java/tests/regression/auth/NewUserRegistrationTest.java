package tests.regression.auth;

import core.BaseTest;
import core.tags.FeatureTags;
import core.tags.RunTags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

@RunTags.Regression
@FeatureTags.Registration

public class NewUserRegistrationTest extends BaseTest  {

    @Test
    @DisplayName("Регистрация нового пользователя")
    void register_new_user_success() {

    }

    @Test
    @DisplayName("Регистрация не удалась из-за пустых обязательных полей")
    void register_fails_with_empty_required_fields() {

    }

    @Test
    @DisplayName("Регистрация завершается неудачей, если пароли не совпадают")
    void register_fails_when_passwords_do_not_match() {

    }

    @Test
    @DisplayName("Регистрация для уже зарегистрированного email не удалась")
    void register_fails_for_existing_email() {

    }

    @Test
    @DisplayName("Заполнение полей регистрации данными в невалидном формате")
    void filling_fields_with_data_in_invalid_format () {

    }

}
