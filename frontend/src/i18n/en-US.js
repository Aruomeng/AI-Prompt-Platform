// 国际化配置文件 - 英文
export default {
  // Common messages
  success: 'Operation successful',
  failed: 'Operation failed',
  error: 'Error',
  warning: 'Warning',
  info: 'Info',
  
  // User related
  user: {
    login: 'Login',
    logout: 'Logout',
    register: 'Register',
    username: 'Username',
    password: 'Password',
    email: 'Email',
    nickname: 'Nickname',
    loginSuccess: 'Login successful',
    logoutSuccess: 'Logout successful',
    registerSuccess: 'Registration successful',
    loginFailed: 'Login failed',
    registerFailed: 'Registration failed',
    userNotFound: 'User not found',
    passwordIncorrect: 'Password incorrect',
    usernameExists: 'Username already exists',
    emailExists: 'Email already exists',
  },
  
  // Prompt related
  prompt: {
    title: 'Title',
    content: 'Content',
    description: 'Description',
    category: 'Category',
    tags: 'Tags',
    create: 'Create',
    edit: 'Edit',
    delete: 'Delete',
    search: 'Search',
    like: 'Like',
    unlike: 'Unlike',
    favorite: 'Favorite',
    unfavorite: 'Unfavorite',
    usage: 'Usage Count',
    createSuccess: 'Created successfully',
    createFailed: 'Creation failed',
    updateSuccess: 'Updated successfully',
    updateFailed: 'Update failed',
    deleteSuccess: 'Deleted successfully',
    deleteFailed: 'Deletion failed',
    likeSuccess: 'Liked successfully',
    unlikeSuccess: 'Unliked successfully',
    favoriteSuccess: 'Favorited successfully',
    unfavoriteSuccess: 'Unfavorited successfully',
    notFound: 'Prompt not found',
    approved: 'Approved',
    pending: 'Pending',
  },
  
  // Category related
  category: {
    name: 'Category Name',
    all: 'All Categories',
    writing: 'Writing Assistant',
    programming: 'Programming Assistant',
    learning: 'Learning Assistant',
    life: 'Life Assistant',
    work: 'Work Assistant',
    creativity: 'Creative Inspiration',
  },
  
  // Buttons and operations
  button: {
    submit: 'Submit',
    cancel: 'Cancel',
    confirm: 'Confirm',
    delete: 'Delete',
    edit: 'Edit',
    save: 'Save',
    clear: 'Clear',
    search: 'Search',
    reset: 'Reset',
    back: 'Back',
    next: 'Next',
    previous: 'Previous',
    close: 'Close',
    open: 'Open',
  },
  
  // Pagination
  pagination: {
    previous: 'Previous',
    next: 'Next',
    total: 'Total',
    items: 'items',
    page: 'Page',
    of: 'of',
    pages: 'pages',
  },
  
  // Validation messages
  validation: {
    required: 'This field is required',
    email: 'Please enter a valid email address',
    minLength: 'Length must be at least {min} characters',
    maxLength: 'Length must not exceed {max} characters',
    passwordMinLength: 'Password must be at least 8 characters',
    passwordRequirements: 'Password must contain uppercase, lowercase, numbers and special characters',
  },
  
  // Confirmation dialogs
  confirm: {
    deletePrompt: 'Are you sure you want to delete this prompt?',
    unfavorite: 'Are you sure you want to remove this from favorites?',
    logout: 'Are you sure you want to logout?',
  },
  
  // Empty states
  empty: {
    noData: 'No data',
    noResults: 'No results found',
    noFavorites: 'No favorites',
    noPrompts: 'No prompts',
  },
  
  // Error messages
  errorMessage: {
    networkError: 'Network error, please check your connection',
    serverError: 'Server error, please try again later',
    unauthorized: 'Unauthorized, please login',
    forbidden: 'Access forbidden',
    notFound: 'Resource not found',
    validationError: 'Data validation failed',
    unknown: 'Unknown error occurred, please try again later',
  },
  
  // Navigation
  nav: {
    home: 'Home',
    promptList: 'Prompt List',
    myPrompts: 'My Prompts',
    myFavorites: 'My Favorites',
    createPrompt: 'Create Prompt',
    admin: 'Admin',
    profile: 'Profile',
    settings: 'Settings',
    logout: 'Logout',
  },
}
