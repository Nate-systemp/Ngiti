import 'package:flutter/material.dart';

void main() {
  runApp(const NgitiApp());
}

class NgitiApp extends StatelessWidget {
  const NgitiApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Ngiti Savings',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(
          seedColor: const Color(0xFFFFB703), // A warm, sunny yellow
          primary: const Color(0xFFFB8500),
          secondary: const Color(0xFF023047),
          brightness: Brightness.light,
        ),
        useMaterial3: true,
      ),
      home: const MainScreen(),
      debugShowCheckedModeBanner: false,
    );
  }
}

class MainScreen extends StatefulWidget {
  const MainScreen({super.key});

  @override
  State<MainScreen> createState() => _MainScreenState();
}

class _MainScreenState extends State<MainScreen> {
  int _currentIndex = 0;

  // Global State (Mocked)
  double targetAmount = 50000.0;
  double currentSaved = 12500.0;
  DateTime targetDate = DateTime.now().add(const Duration(days: 90));

  List<Transaction> transactions = [
    Transaction(amount: 10000, description: 'Started saving', date: DateTime.now().subtract(const Duration(days: 10)), occasion: 'General'),
    Transaction(amount: 2500, description: 'Extra from allowance', date: DateTime.now().subtract(const Duration(days: 2)), occasion: 'Allowance'),
  ];

  List<Debtor> debtors = [
    Debtor(name: 'Juan Dela Cruz', amount: 500, dateBorrowed: DateTime.now().subtract(const Duration(days: 5))),
  ];

  void _addTransaction(Transaction t) {
    setState(() {
      transactions.insert(0, t);
      currentSaved += t.amount;
    });
  }
  
  void _deleteTransaction(Transaction t) {
    setState(() {
      transactions.remove(t);
      currentSaved -= t.amount;
    });
  }

  void _addDebtor(Debtor d) {
    setState(() {
      debtors.add(d);
    });
  }
  
  void _deleteDebtor(Debtor d) {
    setState(() {
      debtors.remove(d);
    });
  }

  @override
  Widget build(BuildContext context) {
    final screens = [
      DashboardView(
        targetAmount: targetAmount,
        currentSaved: currentSaved,
        targetDate: targetDate,
        transactions: transactions,
        onAddTransaction: _addTransaction,
        onDeleteTransaction: _deleteTransaction,
      ),
      DebtorsView(
        debtors: debtors,
        onAddDebtor: _addDebtor,
        onDeleteDebtor: _deleteDebtor,
      ),
    ];

    return Scaffold(
      body: screens[_currentIndex],
      bottomNavigationBar: NavigationBar(
        selectedIndex: _currentIndex,
        onDestinationSelected: (index) {
          setState(() {
            _currentIndex = index;
          });
        },
        destinations: const [
          NavigationDestination(icon: Icon(Icons.savings_outlined), selectedIcon: Icon(Icons.savings), label: 'Ipon'),
          NavigationDestination(icon: Icon(Icons.people_outline), selectedIcon: Icon(Icons.people), label: 'Pautang'),
        ],
      ),
    );
  }
}

class Transaction {
  final double amount;
  final String description;
  final DateTime date;
  final String occasion;
  Transaction({required this.amount, required this.description, required this.date, required this.occasion});
}

class Debtor {
  final String name;
  final double amount;
  final DateTime dateBorrowed;
  Debtor({required this.name, required this.amount, required this.dateBorrowed});
}

// ---------------------------------------------------------
// DASHBOARD VIEW
// ---------------------------------------------------------
class DashboardView extends StatelessWidget {
  final double targetAmount;
  final double currentSaved;
  final DateTime targetDate;
  final List<Transaction> transactions;
  final Function(Transaction) onAddTransaction;
  final Function(Transaction) onDeleteTransaction;

  const DashboardView({
    super.key,
    required this.targetAmount,
    required this.currentSaved,
    required this.targetDate,
    required this.transactions,
    required this.onAddTransaction,
    required this.onDeleteTransaction,
  });

  void _showAddTransactionDialog(BuildContext context) {
    final formKey = GlobalKey<FormState>();
    String description = '';
    double amount = 0;
    String occasion = 'General';
    final occasions = ['General', 'Salary', 'Allowance', 'Gift', 'Bonus'];

    showDialog(
      context: context,
      builder: (context) {
        return AlertDialog(
          title: const Text('Magdagdag ng Ipon'),
          content: Form(
            key: formKey,
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                TextFormField(
                  decoration: const InputDecoration(labelText: 'Halaga (₱)'),
                  keyboardType: TextInputType.number,
                  validator: (val) => val == null || val.isEmpty ? 'Required' : null,
                  onSaved: (val) => amount = double.parse(val!),
                ),
                TextFormField(
                  decoration: const InputDecoration(labelText: 'Ano nangyari? (Description)'),
                  validator: (val) => val == null || val.isEmpty ? 'Required' : null,
                  onSaved: (val) => description = val!,
                ),
                DropdownButtonFormField<String>(
                  value: occasion,
                  decoration: const InputDecoration(labelText: 'Okasyon'),
                  items: occasions.map((o) => DropdownMenuItem(value: o, child: Text(o))).toList(),
                  onChanged: (val) {
                    if (val != null) occasion = val;
                  },
                ),
              ],
            ),
          ),
          actions: [
            TextButton(onPressed: () => Navigator.pop(context), child: const Text('Cancel')),
            ElevatedButton(
              onPressed: () {
                if (formKey.currentState!.validate()) {
                  formKey.currentState!.save();
                  onAddTransaction(Transaction(
                    amount: amount,
                    description: description,
                    date: DateTime.now(),
                    occasion: occasion,
                  ));
                  Navigator.pop(context);
                }
              },
              child: const Text('Save'),
            ),
          ],
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    double progress = (currentSaved / targetAmount).clamp(0.0, 1.0);
    
    String formatCurrency(double val) => '₱${val.toStringAsFixed(2)}';
    String formatDate(DateTime date) => '${date.month}/${date.day}/${date.year}';

    return SafeArea(
      child: CustomScrollView(
        slivers: [
          SliverAppBar(
            expandedHeight: 300.0,
            floating: false,
            pinned: true,
            flexibleSpace: FlexibleSpaceBar(
              title: const Text('Ngiti App', style: TextStyle(fontWeight: FontWeight.bold, color: Colors.white)),
              background: Container(
                padding: const EdgeInsets.all(20),
                decoration: BoxDecoration(
                  gradient: LinearGradient(
                    colors: [Theme.of(context).colorScheme.primary, Theme.of(context).colorScheme.secondary],
                    begin: Alignment.topLeft,
                    end: Alignment.bottomRight,
                  ),
                ),
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    // Mascot placeholder
                    const CircleAvatar(
                      radius: 40,
                      backgroundColor: Colors.white,
                      child: Icon(Icons.sentiment_very_satisfied, size: 55, color: Colors.orange),
                    ),
                    const SizedBox(height: 15),
                    Text(
                      'Goal: ${formatCurrency(targetAmount)}',
                      style: const TextStyle(color: Colors.white70, fontSize: 16),
                    ),
                    Text(
                      formatCurrency(currentSaved),
                      style: const TextStyle(color: Colors.white, fontSize: 36, fontWeight: FontWeight.bold),
                    ),
                    const SizedBox(height: 15),
                    // Animated Progress Bar
                    TweenAnimationBuilder<double>(
                      tween: Tween<double>(begin: 0, end: progress),
                      duration: const Duration(milliseconds: 1500),
                      curve: Curves.easeOutCubic,
                      builder: (context, value, child) {
                        return Column(
                          children: [
                            ClipRRect(
                              borderRadius: BorderRadius.circular(10),
                              child: LinearProgressIndicator(
                                value: value,
                                backgroundColor: Colors.white24,
                                color: const Color(0xFFFFB703),
                                minHeight: 12,
                              ),
                            ),
                            const SizedBox(height: 8),
                            Text('${(value * 100).toStringAsFixed(1)}% Completed', style: const TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
                          ],
                        );
                      },
                    ),
                    const SizedBox(height: 8),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        const Icon(Icons.calendar_month, color: Colors.white70, size: 16),
                        const SizedBox(width: 5),
                        Text('Target Date: ${formatDate(targetDate)}', style: const TextStyle(color: Colors.white70)),
                      ],
                    )
                  ],
                ),
              ),
            ),
          ),
          SliverToBoxAdapter(
            child: Padding(
              padding: const EdgeInsets.all(16.0),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  const Text('History (Ano nangyari?)', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                  IconButton(
                    icon: const Icon(Icons.add_circle, color: Colors.orange, size: 32),
                    onPressed: () => _showAddTransactionDialog(context),
                  ),
                ],
              ),
            ),
          ),
          SliverList(
            delegate: SliverChildBuilderDelegate(
              (context, index) {
                final t = transactions[index];
                return Dismissible(
                  key: Key(t.date.toIso8601String() + t.description + index.toString()),
                  direction: DismissibleDirection.endToStart,
                  onDismissed: (direction) => onDeleteTransaction(t),
                  background: Container(
                    color: Colors.red,
                    alignment: Alignment.centerRight,
                    padding: const EdgeInsets.only(right: 20),
                    child: const Icon(Icons.delete, color: Colors.white),
                  ),
                  child: ListTile(
                    leading: CircleAvatar(
                      backgroundColor: Colors.orange.shade100,
                      child: const Icon(Icons.attach_money, color: Colors.orange),
                    ),
                    title: Text(t.description, style: const TextStyle(fontWeight: FontWeight.bold)),
                    subtitle: Text('${formatDate(t.date)} • ${t.occasion}'),
                    trailing: Text(
                      '+${formatCurrency(t.amount)}',
                      style: const TextStyle(color: Colors.green, fontWeight: FontWeight.bold, fontSize: 16),
                    ),
                  ),
                );
              },
              childCount: transactions.length,
            ),
          ),
          const SliverToBoxAdapter(child: SizedBox(height: 80)), // padding at bottom
        ],
      ),
    );
  }
}

// ---------------------------------------------------------
// DEBTORS VIEW (Mga pinakautangan)
// ---------------------------------------------------------
class DebtorsView extends StatelessWidget {
  final List<Debtor> debtors;
  final Function(Debtor) onAddDebtor;
  final Function(Debtor) onDeleteDebtor;

  const DebtorsView({
    super.key,
    required this.debtors,
    required this.onAddDebtor,
    required this.onDeleteDebtor,
  });

  void _showAddDebtorDialog(BuildContext context) {
    final formKey = GlobalKey<FormState>();
    String name = '';
    double amount = 0;

    showDialog(
      context: context,
      builder: (context) {
        return AlertDialog(
          title: const Text('Add Pautang'),
          content: Form(
            key: formKey,
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                TextFormField(
                  decoration: const InputDecoration(labelText: 'Pangalan'),
                  validator: (val) => val == null || val.isEmpty ? 'Required' : null,
                  onSaved: (val) => name = val!,
                ),
                TextFormField(
                  decoration: const InputDecoration(labelText: 'Halaga (₱)'),
                  keyboardType: TextInputType.number,
                  validator: (val) => val == null || val.isEmpty ? 'Required' : null,
                  onSaved: (val) => amount = double.parse(val!),
                ),
              ],
            ),
          ),
          actions: [
            TextButton(onPressed: () => Navigator.pop(context), child: const Text('Cancel')),
            ElevatedButton(
              onPressed: () {
                if (formKey.currentState!.validate()) {
                  formKey.currentState!.save();
                  onAddDebtor(Debtor(name: name, amount: amount, dateBorrowed: DateTime.now()));
                  Navigator.pop(context);
                }
              },
              child: const Text('Save'),
            ),
          ],
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    String formatCurrency(double val) => '₱${val.toStringAsFixed(2)}';
    String formatDate(DateTime date) => '${date.month}/${date.day}/${date.year}';
    
    double totalUtang = debtors.fold(0, (sum, item) => sum + item.amount);

    return SafeArea(
      child: Column(
        children: [
          Container(
            padding: const EdgeInsets.all(24),
            color: Theme.of(context).colorScheme.secondary,
            width: double.infinity,
            child: Column(
              children: [
                const Icon(Icons.people_alt, color: Colors.white54, size: 40),
                const SizedBox(height: 10),
                const Text('Total na Pautang', style: TextStyle(color: Colors.white70, fontSize: 16)),
                const SizedBox(height: 5),
                Text(
                  formatCurrency(totalUtang),
                  style: const TextStyle(color: Colors.white, fontSize: 36, fontWeight: FontWeight.bold),
                ),
              ],
            ),
          ),
          Padding(
            padding: const EdgeInsets.all(16.0),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                const Text('Listahan ng mga may Utang', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                ElevatedButton.icon(
                  onPressed: () => _showAddDebtorDialog(context),
                  icon: const Icon(Icons.add),
                  label: const Text('Add'),
                  style: ElevatedButton.styleFrom(
                    backgroundColor: Theme.of(context).colorScheme.primary,
                    foregroundColor: Colors.white,
                  ),
                ),
              ],
            ),
          ),
          Expanded(
            child: debtors.isEmpty
                ? const Center(
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Icon(Icons.sentiment_very_satisfied, size: 60, color: Colors.grey),
                        SizedBox(height: 10),
                        Text('Walang may utang sayo. Nice!', style: TextStyle(color: Colors.grey, fontSize: 16)),
                      ],
                    ),
                  )
                : ListView.builder(
                    itemCount: debtors.length,
                    itemBuilder: (context, index) {
                      final d = debtors[index];
                      return Dismissible(
                        key: Key(d.name + d.dateBorrowed.toIso8601String() + index.toString()),
                        direction: DismissibleDirection.endToStart,
                        onDismissed: (direction) => onDeleteDebtor(d),
                        background: Container(
                          color: Colors.green,
                          alignment: Alignment.centerRight,
                          padding: const EdgeInsets.only(right: 20),
                          child: const Icon(Icons.check, color: Colors.white), // Mark as paid
                        ),
                        child: Card(
                          margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 4),
                          child: ListTile(
                            leading: CircleAvatar(
                              backgroundColor: Colors.blue.shade100,
                              child: Text(d.name[0].toUpperCase(), style: const TextStyle(fontWeight: FontWeight.bold)),
                            ),
                            title: Text(d.name, style: const TextStyle(fontWeight: FontWeight.bold)),
                            subtitle: Text('Hiniram noong: ${formatDate(d.dateBorrowed)}'),
                            trailing: Text(
                              formatCurrency(d.amount),
                              style: const TextStyle(color: Colors.redAccent, fontWeight: FontWeight.bold, fontSize: 16),
                            ),
                          ),
                        ),
                      );
                    },
                  ),
          ),
        ],
      ),
    );
  }
}
