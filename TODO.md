# TV Show Module Compilation Errors Todo List

## Database Issues
- [x] Add missing database tables
  - [x] Create `tv_shows` table
  - [x] Create `tv_seasons` table
  - [x] Create `tv_episodes` table
  - [x] Create `tv_show_watch_progress` table

## Entity Registration
- [x] Register entities in the database
  - [x] Add `TvShowEntity` to database entities
  - [x] Add `TvSeasonEntity` to database entities
  - [x] Add `TvEpisodeEntity` to database entities
  - [x] Add `TvShowWatchProgressEntity` to database entities

## DAO Issues
- [x] Fix DAO query issues
  - [x] Fix `getAllTvShows()` query
  - [x] Fix `getTvShowById()` query
  - [x] Fix `deleteTvShow()` query
  - [x] Fix `updateWatchStatus()` query
  - [x] Fix `updateRating()` query
  - [x] Fix `updateComment()` query
  - [x] Fix `searchTvShows()` query
  - [x] Fix `getTvShowsByWatchStatus()` query

## Flow Type Conversion
- [x] Fix Flow type conversion issues
  - [x] Fix `getAllTvShows()` Flow conversion
  - [x] Fix `getTvShowById()` Flow conversion
  - [x] Fix `searchTvShows()` Flow conversion
  - [x] Fix `getTvShowsByWatchStatus()` Flow conversion

## Repository Implementation
- [x] Fix repository implementation issues
  - [x] Fix `TvShowRepositoryImpl` entity mapping
  - [x] Fix `TvShowRepositoryImpl` domain model mapping
  - [x] Fix `TvShowRepositoryImpl` status conversion

## ViewModel Issues
- [ ] Fix ViewModel state handling
  - [x] Fix `TvShowUiState` implementation
  - [ ] Fix state updates in ViewModel methods
  - [ ] Fix error handling in ViewModel

## UI Component Issues
- [ ] Fix UI component issues
  - [ ] Fix `TvShowListScreen` state handling
  - [ ] Fix `TvShowDetailScreen` state handling
  - [ ] Fix `AddTvShowScreen` state handling

## Migration Issues
- [x] Set up proper database migration
  - [x] Update database version
  - [x] Create migration with new schema changes
  - [x] Configure Room to use destructive migration

## Type Conversion Issues
- [x] Fix type conversion issues
  - [x] Add TypeConverters for LocalDate fields
  - [x] Update entity field annotations with proper column names

## Remaining Compile Issues
- [ ] Fix remaining compilation errors
  - [ ] Fix GameEntity duplicate class declarations
  - [ ] Fix MovieRepositoryImpl missing method implementations
  - [ ] Fix TvShowRepositoryImpl parameter mismatches
  - [ ] Fix UI component parameter mismatches
  - [ ] Update ViewModel state handling across the app 