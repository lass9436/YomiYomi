package com.lass.yomiyomi.viewmodel.myParagraph.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lass.yomiyomi.data.repository.MyParagraphRepository
import com.lass.yomiyomi.data.repository.MySentenceRepository
import com.lass.yomiyomi.data.repository.ParagraphListMappingRepository
import com.lass.yomiyomi.data.repository.ParagraphListRepository
import com.lass.yomiyomi.domain.model.entity.ParagraphItem
import com.lass.yomiyomi.domain.model.entity.SentenceItem
import com.lass.yomiyomi.domain.model.constant.Level
import com.lass.yomiyomi.domain.model.entity.ParagraphListItem
import com.lass.yomiyomi.domain.model.entity.ParagraphListMappingItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyParagraphListViewModel @Inject constructor(
    private val myParagraphRepository: MyParagraphRepository,
    private val mySentenceRepository: MySentenceRepository,
    private val paragraphListRepository: ParagraphListRepository,
    private val paragraphListMappingRepository: ParagraphListMappingRepository
) : ViewModel(), MyParagraphListViewModelInterface {

    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedCategory = MutableStateFlow("ALL")
    override val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedLevel = MutableStateFlow<Level>(Level.ALL)
    val selectedLevel: StateFlow<Level> = _selectedLevel.asStateFlow()

    private val _availableLevels = MutableStateFlow<List<Level>>(Level.values().toList())
    val availableLevels: StateFlow<List<Level>> = _availableLevels.asStateFlow()

    private val _allParagraphs = MutableStateFlow<List<ParagraphItem>>(emptyList())
    private val _searchQuery = MutableStateFlow("")

    private val _learningProgress = MutableStateFlow<Map<Int, Float>>(emptyMap())
    val learningProgress: StateFlow<Map<Int, Float>> = _learningProgress.asStateFlow()

    private val _sentenceCounts = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val sentenceCounts: StateFlow<Map<Int, Int>> = _sentenceCounts.asStateFlow()

    // 백그라운드 TTS용 문장 데이터
    private val _sentencesMap = MutableStateFlow<Map<Int, List<SentenceItem>>>(emptyMap())
    val sentencesMap: StateFlow<Map<Int, List<SentenceItem>>> = _sentencesMap.asStateFlow()

    // 문단 리스트 관련 상태
    private val _paragraphLists = MutableStateFlow<List<ParagraphListItem>>(emptyList())
    val paragraphLists: StateFlow<List<ParagraphListItem>> = _paragraphLists.asStateFlow()

    // 현재 문단이 포함된 리스트 ID들
    private val _currentParagraphListIds = MutableStateFlow<List<Int>>(emptyList())
    val currentParagraphListIds: StateFlow<List<Int>> = _currentParagraphListIds.asStateFlow()

    // 각 문단별 리스트 매핑 상태 (Map<문단ID, List<리스트ID>>)
    private val _paragraphListMappings = MutableStateFlow<Map<Int, List<Int>>>(emptyMap())
    val paragraphListMappings: StateFlow<Map<Int, List<Int>>> = _paragraphListMappings.asStateFlow()

    override val paragraphs: StateFlow<List<ParagraphItem>> = combine(
        _allParagraphs,
        _selectedCategory,
        _selectedLevel,
        _searchQuery
    ) { allParagraphs, category, level, query ->
        var filteredParagraphs = if (category == "ALL") {
            allParagraphs
        } else {
            allParagraphs.filter { it.category == category }
        }

        if (level != Level.ALL) {
            filteredParagraphs = filteredParagraphs.filter { it.level == level }
        }

        if (query.isNotBlank()) {
            filteredParagraphs = filteredParagraphs.filter { paragraph ->
                paragraph.title.contains(query, ignoreCase = true) ||
                paragraph.description.contains(query, ignoreCase = true) ||
                paragraph.category.contains(query, ignoreCase = true)
            }
        }

        filteredParagraphs
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init {
        loadParagraphs()
        loadParagraphLists()
        loadAllParagraphListMappings()
    }

    private fun loadParagraphs() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 문장 개수와 함께 조회
                val paragraphList = myParagraphRepository.getParagraphsWithSentenceCounts()
                _allParagraphs.value = paragraphList
                
                // 🔥 문단별 학습 진도도 함께 로드
                val progressMap = mySentenceRepository.getLearningProgressByParagraph()
                _learningProgress.value = progressMap
                
                // 🔥 문단별 문장 개수도 함께 로드
                val countsMap = mySentenceRepository.getSentenceCountsByParagraph()
                _sentenceCounts.value = countsMap
                
                // 🔥 백그라운드 TTS용 문장 데이터 로드
                loadSentencesMap(paragraphList)
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    // 백그라운드 TTS용 문장 데이터 로드
    private suspend fun loadSentencesMap(paragraphs: List<ParagraphItem>) {
        try {
            val sentencesMap = mutableMapOf<Int, List<SentenceItem>>()
            
            paragraphs.forEach { paragraph ->
                val sentences = mySentenceRepository.getSentencesByParagraph(paragraph.paragraphId)
                if (sentences.isNotEmpty()) {
                    sentencesMap[paragraph.paragraphId] = sentences.sortedBy { it.orderInParagraph }
                }
            }
            
            _sentencesMap.value = sentencesMap
        } catch (e: Exception) {
            // Handle error
            _sentencesMap.value = emptyMap()
        }
    }

    override fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setSelectedLevel(level: Level) {
        _selectedLevel.value = level
    }

    override fun searchParagraphs(query: String) {
        _searchQuery.value = query
    }

    // CRUD 기능들
    fun insertParagraph(paragraph: ParagraphItem) {
        viewModelScope.launch {
            try {
                myParagraphRepository.insertParagraph(paragraph)
                loadParagraphs()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun updateParagraph(paragraph: ParagraphItem) {
        viewModelScope.launch {
            try {
                myParagraphRepository.updateParagraph(paragraph)
                loadParagraphs()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun deleteParagraph(paragraphId: Int) {
        viewModelScope.launch {
            try {
                myParagraphRepository.deleteParagraphById(paragraphId)
                loadParagraphs()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    // 🔥 학습 진도 새로고침 메서드 추가
    fun refreshLearningProgress() {
        viewModelScope.launch {
            try {
                val progressMap = mySentenceRepository.getLearningProgressByParagraph()
                _learningProgress.value = progressMap
                
                // 🔥 문장 개수도 함께 새로고침
                val countsMap = mySentenceRepository.getSentenceCountsByParagraph()
                _sentenceCounts.value = countsMap
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    // 특정 문단 조회
    suspend fun getParagraphById(paragraphId: Int): ParagraphItem? {
        return try {
            myParagraphRepository.getParagraphById(paragraphId)
        } catch (e: Exception) {
            null
        }
    }

    private fun loadParagraphLists() {
        viewModelScope.launch {
            try {
                val lists = paragraphListRepository.getAllLists()
                _paragraphLists.value = lists
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    // 문단 리스트 관련 기능들
    fun addNewParagraphList(name: String) {
        viewModelScope.launch {
            try {
                val newList = ParagraphListItem(
                    listId = 0, // Room이 자동으로 ID 생성
                    name = name,
                    description = "",
                    createdAt = System.currentTimeMillis()
                )
                paragraphListRepository.createList(newList)
                loadParagraphLists()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun renameParagraphList(listId: Int, newName: String) {
        viewModelScope.launch {
            try {
                val existingList = paragraphListRepository.getListById(listId)
                existingList?.let {
                    val updatedList = it.copy(name = newName)
                    paragraphListRepository.updateList(updatedList)
                    loadParagraphLists()
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun deleteParagraphList(listId: Int) {
        viewModelScope.launch {
            try {
                paragraphListRepository.deleteList(listId)
                // 매핑도 자동으로 삭제됨 (Room의 CASCADE 설정으로)
                loadParagraphLists()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun addParagraphToLists(paragraph: ParagraphItem, listIds: List<Int>) {
        viewModelScope.launch {
            try {
                listIds.forEach { listId ->
                    val mapping = ParagraphListMappingItem(
                        listId = listId,
                        paragraphId = paragraph.paragraphId,
                        sortOrder = 0, // 기본값
                        createdAt = System.currentTimeMillis()
                    )
                    paragraphListMappingRepository.addMapping(mapping)
                }
                // 리스트 새로고침은 필요 없음 (매핑만 변경되었으므로)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    // 특정 문단이 포함된 리스트 ID들을 로드
    fun loadParagraphListIds(paragraphId: Int) {
        viewModelScope.launch {
            try {
                android.util.Log.d("YomiYomi", "Loading lists for paragraph: $paragraphId")
                val lists = paragraphListMappingRepository.getListsByParagraph(paragraphId)
                val listIds = lists.map { it.listId }
                android.util.Log.d("YomiYomi", "Found lists: $listIds")
                _currentParagraphListIds.value = listIds
            } catch (e: Exception) {
                android.util.Log.e("YomiYomi", "Error loading paragraph list IDs", e)
                _currentParagraphListIds.value = emptyList()
            }
        }
    }

    // 모든 문단의 리스트 매핑 정보를 로드
    private fun loadAllParagraphListMappings() {
        viewModelScope.launch {
            try {
                android.util.Log.d("YomiYomi", "Loading all paragraph list mappings")
                val mappings = mutableMapOf<Int, List<Int>>()
                
                // 모든 문단에 대해 매핑 정보를 가져옴
                _allParagraphs.value.forEach { paragraph ->
                    val lists = paragraphListMappingRepository.getListsByParagraph(paragraph.paragraphId)
                    mappings[paragraph.paragraphId] = lists.map { it.listId }
                }
                
                android.util.Log.d("YomiYomi", "Loaded mappings: $mappings")
                _paragraphListMappings.value = mappings
            } catch (e: Exception) {
                android.util.Log.e("YomiYomi", "Error loading paragraph list mappings", e)
            }
        }
    }

    // 특정 문단의 리스트 매핑 정보를 업데이트
    fun updateParagraphListMappings(paragraph: ParagraphItem, selectedListIds: List<Int>) {
        viewModelScope.launch {
            try {
                android.util.Log.d("YomiYomi", "Updating mappings for paragraph ${paragraph.paragraphId}")
                android.util.Log.d("YomiYomi", "Selected list IDs: $selectedListIds")
                
                val currentListIds = _paragraphListMappings.value[paragraph.paragraphId] ?: emptyList()
                android.util.Log.d("YomiYomi", "Current list IDs: $currentListIds")

                // 현재 매핑과 새로운 매핑을 비교
                val toAdd = selectedListIds - currentListIds.toSet()
                val toRemove = currentListIds - selectedListIds.toSet()

                android.util.Log.d("YomiYomi", "Lists to add: $toAdd")
                android.util.Log.d("YomiYomi", "Lists to remove: $toRemove")

                // 새로운 매핑 추가
                toAdd.forEach { listId ->
                    val mapping = ParagraphListMappingItem(
                        listId = listId,
                        paragraphId = paragraph.paragraphId,
                        sortOrder = 0,
                        createdAt = System.currentTimeMillis()
                    )
                    paragraphListMappingRepository.addMapping(mapping)
                }

                // 기존 매핑 제거
                toRemove.forEach { listId ->
                    paragraphListMappingRepository.removeMapping(listId, paragraph.paragraphId)
                }

                // 상태 업데이트
                val newMappings = _paragraphListMappings.value.toMutableMap()
                newMappings[paragraph.paragraphId] = selectedListIds
                _paragraphListMappings.value = newMappings
            } catch (e: Exception) {
                android.util.Log.e("YomiYomi", "Error updating paragraph list mappings", e)
            }
        }
    }
} 
