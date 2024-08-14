import com.example.api.dtos.category.CategoryDto
import com.example.api.dtos.event.EventDto
import com.example.core.models.category.CategoryModel
import com.example.core.models.event.EventModel
import org.junit.Assert.assertEquals
import org.junit.Test

class MapperTest {

    @Test
    fun categoryDtoMapper_Mapped_GetCategoryModel() {
        val categoryDto = CategoryDto("id", "name", "nameEn", "imageUrl")
        val categoryModel = CategoryModel(
            "id", "name", "nameEn", "imageUrl",
            isSelected = true
        )

        val mappedCategory = categoryDto.mapToDomain()

        assertEquals(
            categoryModel,
            mappedCategory,
        )
    }

    @Test
    fun eventsDtoMapper_Mapped_GetCategoryModel() {
        val eventDto = EventDto(
            "id", "name", 1, 2,
            "description", 3, "photo", "category",
            4, "phone", "address", "email",
            "organization", "url",
        )
        val eventModel = EventModel(
            "id", "name", 1, 2,
            "description", 3, "photo", "category",
            4, "phone", "address", "email",
            "organization", "url", isRead = false
        )

        val mappedEvent = eventDto.mapToDomain()

        assertEquals(
            eventModel,
            mappedEvent,
        )
    }
}